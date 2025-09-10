package com.lm.order.service.impl;

import com.lm.message.OrderCreateMessage;
import com.lm.message.OrderTimeoutMessage;
import com.lm.order.Eumn.OrderStatus;
import com.lm.order.domain.Order;
import com.lm.order.domain.OrderItem;
import com.lm.order.dto.OrderSubmitDTO;
import com.lm.order.dto.OrderSubmitTestDTO;
import com.lm.order.feign.*;
import com.lm.order.mapper.OrderMapper;
import com.lm.order.service.OrderService;
import com.lm.order.utils.OrderNoGenerator;
import com.lm.order.vo.OrderVO;
import com.lm.product.vo.ProductSkuVO;
import com.lm.promotion.dto.LockCouponsDTO;
import com.lm.utils.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Resource
    private UserFeignClient userFeign;

    @Autowired
    PaymentFeignClient paymentFeignClient;
    @Autowired
    CouponFeignClient couponFeignClient;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ProductFeignClient productFeignClient; // 商品服务Feign接口

    @Autowired
    private StockFeignClient stockFeignClient;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 秒杀下订单，不用seata，太重
     * 普通下订单，直接用seata就行，不用搞什么mq
     *
     * @param dto
     * @return
     */

    @Override
    @Transactional
//    @GlobalTransactional(name = "createOrder", rollbackFor = Exception.class) // 重点注解
    public OrderVO submitOrder(OrderSubmitDTO dto) {

        // 1. 参数校验
//    - 校验 merchantId 是否存在、合法
//    - 校验 orderItems 不为空，数量大于 0
//    - 校验 receiverInfoId 是否存在（调用用户服务 / 地址服务）
//    - 校验 payType 是否支持（1 微信 / 2 支付宝 / 3 余额等）


        Long userId = UserContextHolder.getUser().getId();
        if (userId == null) throw new RuntimeException("用户未登录");
        Long merchantId = dto.getMerchantId();


        // 2. 校验用户信息，这里不光是校验地址。应该有收货人的姓名，电话，地址
        // 一个用户有多个收货信息,所以校验用户id和收货信息id是否匹配
        boolean exist = userFeign.verifyAddressBelongsToUser(userId, dto.getReceiverInfoId());
        if (!exist) throw new RuntimeException("地址无效");

        // 3. 获取商品库存信息,用sku
        List<OrderItem> items = dto.getOrderItems();
        if (items == null || items.isEmpty()) throw new RuntimeException("订单项为空");


        // 2. 计算价格（前端传过来的金额要校验，不能全信）
//    - 遍历 orderItems，查询商品服务获取单价、库存、上下架状态
//    - 计算订单总价 totalAmount
//    - 应用优惠券 couponUserIds（调用优惠券服务校验有效性）
//    - 应用满减活动 fullDiscountId（调用营销活动服务）
//    - 得到实际应付金额 payAmount
//    - 校验 dto.payAmount 与计算后的金额是否一致，避免前端篡改

        BigDecimal totalAmount = BigDecimal.ZERO;


        for (OrderItem item : items) {
            // 校验商品价格
            ProductSkuVO skuInfo = productFeignClient.getSkuInfo(item.getSkuId());
            if (skuInfo == null) {
                throw new RuntimeException("商品不可售: " + item.getSkuId());
            }

            if (!skuInfo.getPrice().equals(item.getPrice())) {
                throw new RuntimeException("商品价格有变动");
            }

            // 使用数据库价格覆盖前端传来的价格
            BigDecimal dbPrice = skuInfo.getPrice();
            BigDecimal itemTotal = dbPrice.multiply(new BigDecimal(item.getQuantity()));
            item.setPrice(dbPrice);
            item.setTotalPrice(itemTotal);

            totalAmount = totalAmount.add(itemTotal);

        }

        List<Long> couponUserIds = dto.getCouponUserIds();
        // 1. 生成订单号
        String orderNo = OrderNoGenerator.generateOrderNo(userId, merchantId);

        try {
   /*
           优惠券，我直接假设，只有满减优惠券，可以叠加使用，就这样
            */
            BigDecimal discountAmount = BigDecimal.ZERO; // 优惠金额


            // 2. 校验并锁定优惠券   预锁（Lock） - status: 0 → 1
            if (couponUserIds != null && !couponUserIds.isEmpty()) {
                // 加锁并计算优惠金额
                LockCouponsDTO lockRequest = new LockCouponsDTO();
                lockRequest.setCouponUserIds(dto.getCouponUserIds());
                lockRequest.setUserId(userId);
                lockRequest.setMerchantId(dto.getMerchantId());
                lockRequest.setOrderAmount(totalAmount);

                //这里加判断
                BigDecimal couponDiscount = couponFeignClient.lockAndCalc(lockRequest);

                discountAmount = discountAmount.add(couponDiscount);
            }


            // 4. 计算应付金额
            BigDecimal payAmount = totalAmount.subtract(discountAmount);
            if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                payAmount = BigDecimal.ZERO;
            }

            // 5. 金额校验（防止篡改）
            if (dto.getPayAmount().compareTo(payAmount) != 0) {
                throw new RuntimeException("订单金额校验失败，可能被篡改");
            }


// 4. 创建订单（本地事务）
//    - 生成全局唯一订单号（雪花算法 / 分布式 ID）
//    - 插入订单表（order），状态设为：待支付
//    - 插入订单明细表（order_item）
//    - 插入优惠券使用记录（如果有）


            /**
             *     这两支付完再填充
             *     private int payType; // 支付方式：1微信 2支付宝 3余额等
             *     private LocalDateTime payTime;
             *     private int status; // 0待付款 1已付款 2已发货 3已收货 4已关闭 5退款中 6已退款
             *     private int orderType; // 0普通订单 1秒杀 2团购等
             *     private String cancelReason;
             *     private LocalDateTime cancelTime;
             *     private Long receiverInfoId;
             */
            // 2. 插入订单表
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setMerchantId(merchantId);
            order.setTotalAmount(totalAmount);
            order.setPayAmount(payAmount);

            order.setStatus(OrderStatus.PRE_CREATE.getCode()); // 预创建
            order.setOrderType(0); // 普通订单

            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());

            order.setReceiverInfoId(dto.getReceiverInfoId());
            orderMapper.insertOrder(order);


            // 3. 插入订单明细表
            for (OrderItem item : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo(orderNo);
                orderItem.setSkuId(item.getSkuId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                orderMapper.insertOrderItem(orderItem);
            }
            log.info("订单创建成功，orderNo={}", orderNo);


//        // 1. 预扣库存（在Redis中暂时锁定）
//        boolean preDeductSuccess = stockFeignClient.preDeductStock(dto.getItems());
//        if (!preDeductSuccess) {
//            throw new RuntimeException("库存不足");
//        }


// 5. 发送事务消息（RocketMQ）
//    - 发送半消息到 "order-create-topic"
//    - 本地事务提交成功后，确认消息提交
//    - 消费方：库存服务（扣减库存）、优惠券服务（锁定优惠券）、营销服务（锁定活动名额）
//    - 注意幂等性：消费方要能处理重复消息

            OrderCreateMessage message = new OrderCreateMessage();
            message.setOrderNo(orderNo);
            message.setUserId(userId);
            message.setOrderSubmitDTO(dto);
            message.setLockedCouponIds(couponUserIds);
            message.setCreateTime(LocalDateTime.now());

            // 4. 发送事务消息
            // 发送事务消息 - 传入订单号作为arg参数
            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
                    "ORDER_CREATE_TOPIC:ORDER_CREATE",
                    MessageBuilder.withPayload(message)  // 消息体
                            .setHeader(MessageConst.PROPERTY_KEYS, orderNo)
                            .build(),
                    orderNo  // 这里传入订单号，不是整个dto！
            );

            log.info("事务消息发送完成，状态: {}, msgId: {}",
                    sendResult.getSendStatus(), sendResult.getMsgId());

            // 5. 根据发送结果处理
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new RuntimeException("消息发送失败，订单创建终止");
            }


            // 6. 构建返回结果
            OrderVO vo = new OrderVO();
            vo.setOrderNo(orderNo);
            vo.setTotalAmount(totalAmount);
            vo.setPayAmount(payAmount);
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());


            // 发送超时消息（30分钟）
            sendOrderTimeoutMessage(orderNo, 30);


            return vo;


        } catch (RuntimeException e) {
            log.error("订单提交失败，orderSn: {}", orderNo, e);

            // 7. 失败时释放锁定的优惠券
            if (couponUserIds != null && !couponUserIds.isEmpty()) {
                try {
                    couponFeignClient.cancelLockCoupons(couponUserIds);
                } catch (Exception ex) {
                    log.error("释放优惠券失败", ex);
                }
            }

            throw new RuntimeException("下单失败: " + e.getMessage());
        }


    }

    // 在订单创建成功后发送延迟消息
    private void sendOrderTimeoutMessage(String orderNo, int timeoutMinutes) {
        try {
            OrderTimeoutMessage timeoutMessage = new OrderTimeoutMessage();
            timeoutMessage.setOrderNo(orderNo);

            // RocketMQ延时级别：16=30分钟
            int delayLevel = 16;

            rocketMQTemplate.syncSend(
                    "ORDER_TIMEOUT_TOPIC:ORDER_TIMEOUT",
                    MessageBuilder.withPayload(timeoutMessage)
                            .setHeader(MessageConst.PROPERTY_KEYS, orderNo)
                            .build(),
                    3000,      // 发送超时时间 ms
                    delayLevel // 延迟级别
            );
            log.info("订单超时消息发送成功，orderNo={}, delayLevel={}", orderNo, delayLevel);
        } catch (Exception e) {
            log.error("发送订单超时消息失败 orderNo={}", orderNo, e);
            // TODO: 可以考虑 fallback，比如记录到 DB，定时任务兜底
        }
    }


    @Override
    @Transactional
//    @GlobalTransactional(name = "createOrder", rollbackFor = Exception.class) // 重点注解
    public OrderVO submitOrderTest(OrderSubmitTestDTO orderSubmitTestDTO) {

        // 1. 参数校验
//    - 校验 merchantId 是否存在、合法
//    - 校验 orderItems 不为空，数量大于 0
//    - 校验 receiverInfoId 是否存在（调用用户服务 / 地址服务）
//    - 校验 payType 是否支持（1 微信 / 2 支付宝 / 3 余额等）

        Long userId = orderSubmitTestDTO.getUserId();


        OrderSubmitDTO dto = new OrderSubmitDTO();
        dto.setMerchantId(orderSubmitTestDTO.getMerchantId());
        dto.setOrderItems(orderSubmitTestDTO.getOrderItems());
        dto.setPayAmount(orderSubmitTestDTO.getPayAmount());
        dto.setReceiverInfoId(orderSubmitTestDTO.getReceiverInfoId());
        dto.setCouponUserIds(orderSubmitTestDTO.getCouponUserIds());
        dto.setTotalAmount(orderSubmitTestDTO.getTotalAmount());
        dto.setOrderType(orderSubmitTestDTO.getOrderType());
        dto.setRemark(orderSubmitTestDTO.getRemark());
        dto.setPayType(orderSubmitTestDTO.getPayType());
        dto.setFullDiscountId(orderSubmitTestDTO.getFullDiscountId());


//        Long userId = UserContextHolder.getUser().getId();
        if (userId == null) throw new RuntimeException("用户未登录");
        Long merchantId = dto.getMerchantId();


        // 2. 校验用户信息，这里不光是校验地址。应该有收货人的姓名，电话，地址
        // 一个用户有多个收货信息,所以校验用户id和收货信息id是否匹配
//        boolean exist = userFeign.verifyAddressBelongsToUser(userId, dto.getReceiverInfoId());
//        if (!exist) throw new RuntimeException("地址无效");

        // 3. 获取商品库存信息,用sku
        List<OrderItem> items = dto.getOrderItems();
        if (items == null || items.isEmpty()) throw new RuntimeException("订单项为空");


        // 2. 计算价格（前端传过来的金额要校验，不能全信）
//    - 遍历 orderItems，查询商品服务获取单价、库存、上下架状态
//    - 计算订单总价 totalAmount
//    - 应用优惠券 couponUserIds（调用优惠券服务校验有效性）
//    - 应用满减活动 fullDiscountId（调用营销活动服务）
//    - 得到实际应付金额 payAmount
//    - 校验 dto.payAmount 与计算后的金额是否一致，避免前端篡改

        BigDecimal totalAmount = BigDecimal.ZERO;


        for (OrderItem item : items) {
            // 校验商品价格
            ProductSkuVO skuInfo = productFeignClient.getSkuInfo(item.getSkuId());
            if (skuInfo == null) {
                throw new RuntimeException("商品不可售: " + item.getSkuId());
            }

            if (!skuInfo.getPrice().equals(item.getPrice())) {
                throw new RuntimeException("商品价格有变动");
            }

            // 使用数据库价格覆盖前端传来的价格
            BigDecimal dbPrice = skuInfo.getPrice();
            BigDecimal itemTotal = dbPrice.multiply(new BigDecimal(item.getQuantity()));
            item.setPrice(dbPrice);
            item.setTotalPrice(itemTotal);

            totalAmount = totalAmount.add(itemTotal);

        }

        List<Long> couponUserIds = dto.getCouponUserIds();
        // 1. 生成订单号
        String orderNo = OrderNoGenerator.generateOrderNo(userId, merchantId);

        try {
   /*
           优惠券，我直接假设，只有满减优惠券，可以叠加使用，就这样
            */
            BigDecimal discountAmount = BigDecimal.ZERO; // 优惠金额


            // 2. 校验并锁定优惠券   预锁（Lock） - status: 0 → 1
            if (couponUserIds != null && !couponUserIds.isEmpty()) {
                // 加锁并计算优惠金额
                LockCouponsDTO lockRequest = new LockCouponsDTO();
                lockRequest.setCouponUserIds(dto.getCouponUserIds());
                lockRequest.setUserId(userId);
                lockRequest.setMerchantId(dto.getMerchantId());
                lockRequest.setOrderAmount(totalAmount);

                //这里加判断
                BigDecimal couponDiscount = couponFeignClient.lockAndCalc(lockRequest);

                discountAmount = discountAmount.add(couponDiscount);
            }


            // 4. 计算应付金额
            BigDecimal payAmount = totalAmount.subtract(discountAmount);
            if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                payAmount = BigDecimal.ZERO;
            }

            // 5. 金额校验（防止篡改）
            if (dto.getPayAmount().compareTo(payAmount) != 0) {
                throw new RuntimeException("订单金额校验失败，可能被篡改");
            }


// 4. 创建订单（本地事务）
//    - 生成全局唯一订单号（雪花算法 / 分布式 ID）
//    - 插入订单表（order），状态设为：待支付
//    - 插入订单明细表（order_item）
//    - 插入优惠券使用记录（如果有）


            /**
             *     这两支付完再填充
             *     private int payType; // 支付方式：1微信 2支付宝 3余额等
             *     private LocalDateTime payTime;
             *     private int status; // 0待付款 1已付款 2已发货 3已收货 4已关闭 5退款中 6已退款
             *     private int orderType; // 0普通订单 1秒杀 2团购等
             *     private String cancelReason;
             *     private LocalDateTime cancelTime;
             *     private Long receiverInfoId;
             */
            // 2. 插入订单表
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setMerchantId(merchantId);
            order.setTotalAmount(totalAmount);
            order.setPayAmount(payAmount);

            order.setStatus(OrderStatus.PRE_CREATE.getCode()); // 预创建
            order.setOrderType(0); // 普通订单

            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());

            order.setReceiverInfoId(dto.getReceiverInfoId());
            orderMapper.insertOrder(order);


            // 3. 插入订单明细表
            for (OrderItem item : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo(orderNo);
                orderItem.setSkuId(item.getSkuId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                orderMapper.insertOrderItem(orderItem);
            }
            log.info("订单创建成功，orderNo={}", orderNo);


//        // 1. 预扣库存（在Redis中暂时锁定）
//        boolean preDeductSuccess = stockFeignClient.preDeductStock(dto.getItems());
//        if (!preDeductSuccess) {
//            throw new RuntimeException("库存不足");
//        }


// 5. 发送事务消息（RocketMQ）
//    - 发送半消息到 "order-create-topic"
//    - 本地事务提交成功后，确认消息提交
//    - 消费方：库存服务（扣减库存）、优惠券服务（锁定优惠券）、营销服务（锁定活动名额）
//    - 注意幂等性：消费方要能处理重复消息

            OrderCreateMessage message = new OrderCreateMessage();
            message.setOrderNo(orderNo);
            message.setUserId(userId);
            message.setOrderSubmitDTO(dto);
            message.setLockedCouponIds(couponUserIds);
            message.setCreateTime(LocalDateTime.now());

            // 4. 发送事务消息
            // 发送事务消息 - 传入订单号作为arg参数
            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
                    "ORDER_CREATE_TOPIC:ORDER_CREATE",
                    MessageBuilder.withPayload(message)  // 消息体
                            .setHeader(MessageConst.PROPERTY_KEYS, orderNo)
                            .build(),
                    orderNo  // 这里传入订单号，不是整个dto！
            );

            log.info("事务消息发送完成，状态: {}, msgId: {}",
                    sendResult.getSendStatus(), sendResult.getMsgId());

            // 5. 根据发送结果处理
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                throw new RuntimeException("消息发送失败，订单创建终止");
            }



            // 6. 构建返回结果
            OrderVO vo = new OrderVO();
            vo.setOrderNo(orderNo);
            vo.setTotalAmount(totalAmount);
            vo.setPayAmount(payAmount);
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());


            // 发送超时消息（30分钟）
            sendOrderTimeoutMessage(orderNo, 30);


            return vo;








            //TODO

            // 5. 【新增】创建支付订单（在下单成功后）
            PaymentOrderDTO paymentOrder = paymentService.createPayment(
                    order.getId(),
                    dto.getUserId(),
                    dto.getPayType() // 支付方式：ALIPAY, WECHAT
            );
            // 设置支付信息
            vo.setPaymentId(paymentOrder.getPaymentId());
            vo.setPayUrl(paymentOrder.getPayUrl());
            vo.setQrCode(paymentOrder.getQrCode());
            vo.setExpireMinutes(30); // 30分钟支付过期



        } catch (RuntimeException e) {
            log.error("订单提交失败，orderSn: {}", orderNo, e);

            // 7. 失败时释放锁定的优惠券
            if (couponUserIds != null && !couponUserIds.isEmpty()) {
                try {
                    couponFeignClient.cancelLockCoupons(couponUserIds);
                } catch (Exception ex) {
                    log.error("释放优惠券失败", ex);
                }
            }

            throw new RuntimeException("下单失败: " + e.getMessage());
        }


    }
}
