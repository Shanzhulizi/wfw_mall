package com.lm.order.service.impl;

import com.lm.common.R;
import com.lm.mq.StockDeductMessage;
import com.lm.order.mq.sender.StockMQSender;
import com.lm.order.service.OrderService;
import com.lm.order.service.StockLuaService;
import com.lm.order.domain.Order;
import com.lm.order.domain.OrderItem;
import com.lm.order.dto.OrderSubmitDTO;
import com.lm.order.feign.ProductFeignClient;
import com.lm.order.feign.UserFeignClient;
import com.lm.order.mapper.OrderMapper;
import com.lm.order.utils.OrderNoGenerator;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lm.common.constant.MQConstant.STOCK_DEDUCT_ROUTING_KEY;
import static com.lm.common.constant.MQConstant.STOCK_EVENT_EXCHANGE;
import static com.lm.common.constant.RedisConstants.STOCK_KEY_PREFIX;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductFeignClient productFeign;

    @Autowired
    private StockLuaService stockLuaService;

    @Autowired
    private UserFeignClient userFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StockMQSender stockMQSender;

    /**
     * |→ 生成订单号
     * |→ 调用【商品服务】校验价格和库存
     * |→ 调用【用户服务】获取用户信息(顺便获取地址)
     * |→ 调用【优惠券服务】校验并锁定优惠券
     * |→ Redis 扣减库存（原子 Lua 脚本）
     * |→ 异步发送下单消息到 MQ（下单队列）
     * ↓
     * 【订单服务消费者】消费消息创建订单 + 插入数据库(消费者落库（幂等校验 + 订单入库）)
     * ↓
     * 返回订单ID，前端跳转支付页
     * ↓
     * 用户付款后【支付服务】回调
     * ↓
     * 更新订单状态 → 通知发货 → 推送消息等
     */
    @Override
    @Transactional
    public R submitOrder(OrderSubmitDTO dto) {
        Long userId = UserContextHolder.getUser().getId();
        if (userId == null) return R.error("用户未登录");
        Long merchantId = dto.getMerchantId();

        // 1. 生成订单号
        String orderNo = OrderNoGenerator.generateOrderNo(userId, merchantId);

        // 2. 校验用户信息，这里不光是校验地址。应该有收货人的姓名，电话，地址
        // 一个用户有多个收货信息,所以校验用户id和收货信息id是否匹配
        boolean exist = userFeign.verifyAddressBelongsToUser(userId, dto.getReceiverInfoId());
        if (!exist) return R.error("地址无效");

        // 3. 获取商品库存信息,用sku
        List<OrderItem> items = dto.getOrderItems();
        if (items == null || items.isEmpty()) return R.error("订单项不能为空");

        // 只校验商品价格(库存在lua中一次性校验)
        // 怎么才能避免用for循环来一个个调用商品服务校验呢？
        // 1. 提取 skuId 列表
        List<Long> skuIds = items.stream()
                .map(OrderItem::getSkuId)
                .collect(Collectors.toList());
        // 2. 批量调用商品服务
        List<ProductPriceValidationDTO> productList = productFeign.getProductPriceValidationDTOsByIds(skuIds);
        // 3. 映射为 Map<skuId, ProductDTO>
        Map<Long, ProductPriceValidationDTO> productMap = productList.stream()
                .collect(Collectors.toMap(ProductPriceValidationDTO::getSkuId, p -> p));
        for (OrderItem item : items) {
            // 校验商品价格
            ProductPriceValidationDTO pv = productMap.get(item.getSkuId());
            if (pv == null) {
                return R.error("商品不存在");
            }
            if (!pv.getPrice().equals(item.getPrice())) {
                return R.error("商品价格有变动");
            }
        }

        // 4. 校验并冻结优惠券
//        couponFeign.lockCoupon(dto.getCouponId(), userId);


        List<String> keys = new ArrayList<>();
        List<Integer> buyNums = new ArrayList<>();

        for (OrderItem item : items) {
            // 添加到扣减库存的 keys 和数量列表
            keys.add(STOCK_KEY_PREFIX + item.getSkuId());
            buyNums.add(item.getQuantity());
        }
        boolean success = stockLuaService.deductStock(keys, buyNums);
        if (!success) {
            log.info("redis扣减库存失败");
            return R.error("redis扣减库存失败");
        }


        log.info("redis扣减库存成功");
        //TODO 发送扣减库存消息到库存服务，库存服务执行数据库扣减

        // 发送消息
        log.info("发送扣减库存消息");
        stockMQSender.sendStockDeductMessage(orderNo, items);


        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        //总金额和实付金额让前端算好往后传
        order.setTotalAmount(dto.getTotalAmount());
        order.setPayAmount(dto.getPayAmount());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
// 0普通订单
        order.setOrderType(0);
        order.setStatus(0); // 0待付款

        order.setReceiverInfoId(dto.getReceiverInfoId());

        log.info("提交订单,{}", order);
        //TODO 发送订单消息到 MQ（状态：待支付）


//        // 6. 构造订单消息并发送到 MQ（状态：待支付）
//        OrderMQMessage msg = new OrderMQMessage(dto, userId);
//        rabbitTemplate.convertAndSend("order.exchange", "order.create", msg);
//
//        // 7. 生成支付单（调用支付服务）
//        private int payType; // 支付方式：1微信 2支付宝 3余额等
//        private LocalDateTime payTime;
//        PaymentDTO paymentDTO = new PaymentDTO();
//        paymentDTO.setOrderNo(orderNo);
//        paymentDTO.setAmount(totalAmount);
//        paymentDTO.setPayType(orderDTO.getPayType()); // 支付宝/微信
//        Result<PaymentVO> paymentResult = paymentFeignClient.createPayment(paymentDTO);
//
//        // 8. 返回订单信息+支付链接
//        OrderVO orderVO = convert(order);
//        orderVO.setPayUrl(paymentResult.getData().getPayUrl());
//        return orderVO;

        //补充订单信息（用sku）


        return null;
    }


}
