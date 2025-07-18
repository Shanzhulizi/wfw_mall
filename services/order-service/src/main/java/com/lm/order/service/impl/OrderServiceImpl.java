package com.lm.order.service.impl;

import com.lm.common.R;
import com.lm.mq.StockDeductMessage;
import com.lm.order.domain.OrderShipping;
import com.lm.order.dto.CouponUseDTO;
import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.order.feign.CouponFeignClient;
import com.lm.order.feign.PaymentFeignClient;
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
import com.lm.order.vo.OrderVO;
import com.lm.payment.dto.PaymentDTO;
import com.lm.payment.dto.PaymentInfoDTO;
import com.lm.payment.dto.PaymentResultDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Autowired
    PaymentFeignClient paymentFeignClient;
    @Autowired
    CouponFeignClient couponFeignClient;

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
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            // 校验商品价格
            ProductPriceValidationDTO pv = productMap.get(item.getSkuId());
            if (pv == null) {
                return R.error("商品不存在");
            }
            if (!pv.getPrice().equals(item.getPrice())) {
                return R.error("商品价格有变动");
            }
            /**
             * 优惠券的话，我觉得，因为订单里的各个商品的店铺都可能给一些优惠券，
             * 然后平台也会给一些通用的优惠券，所以这里为了简单，我们就假设店铺不会给优惠券
             */
            //TODO 这里假设了本项目的优惠券只有平台优惠券，没有店铺优惠券
            totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));


        }

        //TODO
        // 4. 校验并冻结优惠券
//        couponFeign.lockCoupon(dto.getCouponId(), userId);
        // 优惠之后的价格
        // TODO 可能有满减，可能有折扣
        List<CouponUseDTO> coupons = dto.getCoupons();
        // 4. 校验并冻结优惠券
        try {
            couponFeignClient.lockCoupon(dto.getCoupons(), userId);
        } catch (Exception e) {
//            throw new BusinessException("优惠券锁定失败，无法提交订单");
        }

        BigDecimal payAmount = totalAmount;

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
//        TODO 保证发送的数据库扣减库存消息一定能到达

        // 发送消息
        log.info("发送扣减库存消息");
        stockMQSender.sendStockDeductMessage(orderNo, items);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        //总金额和实付金额让前端算好往后传
        // 不对，前面之所以校验金额，就是因为前端传来的可能是被修改过的
        // 所以，这里还得算总金额和优惠后的金额
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setStatus(dto.getOrderType()); // 0待付款
        order.setReceiverInfoId(dto.getReceiverInfoId());

        log.info("提交订单,{}", order);
        // 下单流程需要确保“下单成功”的结果立即返回给用户，用户体验必须快、确定。

        // 用消息队列异步处理订单写库，会带来两个问题
        /*
        ❌ 问题 1：用户体验不确定
            如果你把订单入库也丢到 MQ 异步处理，用户下单时你就只能告诉他：
            “我们正在处理您的订单，请稍后查看订单状态。”
            但你没法立即知道订单到底有没有落库、有没有成功。电商里这是不接受的。

        ❌ 问题 2：订单编号无法快速返回
            通常你要立即返回：

            {
              "code": 200,
              "message": "下单成功",
              "data": {
                "orderNo": "202507160001",
                "payUrl": "https://pay.xxx.com/order/202507160001"
              }
            }
            这个订单号需要你同步 insert 后再拿来返回。
         */
        /*
            在高并发下，不会冲垮数据库吗?
            在高并发场景下如果直接把“订单写库”暴露在前端请求中，确实存在数据库压力过大的风险。
            但是，大型电商平台并不会单纯依赖数据库扛压，而是通过一整套架构手段解决这个问题。

            正确姿势：限流 + 削峰 + 异步 + 分库分表 + 缓存前置
         */


        // 不仅要操作order表，还要插入order_item，order_shopping


        /**
         * 加个判断，是普通订单还是秒杀订单
         * 注意这里的区分仅仅是为了能够区分，二者相应的操作是完全一致的
         * 只不过，在秒杀中，需通过 “订单表简化、分库分表、全链路削峰” 等手段降低数据库压力，同时将非核心操作异步化。
         */
        if (dto.getOrderType() == 1) {
            order.setOrderType(1); // 秒杀订单
            //直接操作数据库

//        // 6. 构造订单消息并发送到 MQ（状态：待支付）
//        OrderMQMessage msg = new OrderMQMessage(dto, userId);
//        rabbitTemplate.convertAndSend("order.exchange", "order.create", msg);
        } else {
            order.setOrderType(0); // 普通订单

            //直接操作数据库
            // 6. 插入订单到数据库
            orderMapper.insertOrder(order);
            if (order.getId() == null) {
                return R.error("订单创建失败");
            }
            // 获取订单ID
            Long orderId = order.getId();

            OrderShipping orderShipping = new OrderShipping();
            orderShipping.setOrderId(order.getId());
            //远程调用user服务，根据receiverInfoId获取收货人信息

            ReceiverInfoDTO receiverInfo = userFeign.getReceiveInfoBy(dto.getReceiverInfoId());
            orderShipping.setReceiverName(receiverInfo.getReceiverName());
            orderShipping.setPhone(receiverInfo.getPhone());
            orderShipping.setProvince(receiverInfo.getProvince());
            orderShipping.setCity(receiverInfo.getCity());
            orderShipping.setArea(receiverInfo.getArea());
            orderShipping.setDetailAddress(receiverInfo.getDetail_address());

            orderMapper.insertOrderShipping(orderShipping);


//            for (OrderItem item : items) {
//                OrderItem orderItem = new OrderItem();
//                orderMapper.insertOrderItem(orderItem);
//            }

            for (OrderItem item : items) {
                item.setOrderId(orderId);
            }
            orderMapper.insertOrderItems(items);

        }

        // 7. 生成支付单（调用支付服务）

        PaymentInfoDTO paymentInfoDTO = new PaymentInfoDTO();
        paymentInfoDTO.setOrderNo(orderNo);
        paymentInfoDTO.setPayAmount(payAmount);
        PaymentInfoDTO paymentDTO = paymentFeignClient.createPayment(paymentInfoDTO);


        // 8. 返回订单信息+支付链接
        OrderVO orderVO = convert(order);
        orderVO.setPayUrl(paymentDTO.getPayUrl());

        //补充订单信息（用sku）

        return R.ok("创建订单成功", orderVO);

    }

    private OrderVO convert(Order order) {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(order.getId());
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setUserId(order.getUserId());
        orderVO.setMerchantId(order.getMerchantId());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setPayAmount(order.getPayAmount());
        orderVO.setCreateTime(order.getCreateTime());
        orderVO.setUpdateTime(order.getUpdateTime());
        orderVO.setStatus(order.getStatus());
        orderVO.setReceiverInfoId(order.getReceiverInfoId());
        orderVO.setRemark(order.getRemark());
        order.setPayType(order.getPayType());
        // 其他字段可以根据需要添加
        return orderVO;

    }


}
