//package com.lm.order.mq.listener;
//
//import com.lm.order.Eumn.OrderStatus;
//import com.lm.order.mapper.OrderMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
//import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//
///**
// * Listener 的关键特点：
// *
// * 只处理本地数据库事务
// *
// * 不关心业务逻辑，只关心"本地操作是否成功"
// *
// * 在生产者端执行
// *
// * 使用 @Transactional 管理本地数据库
// */
//@Slf4j
//@RocketMQTransactionListener
//public class OrderTransactionListener implements RocketMQLocalTransactionListener {
//
//    @Autowired
//    private OrderMapper orderMapper;
//    /**
//     * 执行本地事务 - 消息发送后立即回调
//     */
//    @Override
//    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
//        String orderNo = (String) arg;
//        try {
//            orderMapper.updateStatus(orderNo, OrderStatus.CREATING.getCode());
//            log.info("本地事务执行成功: {}", orderNo);
//            return RocketMQLocalTransactionState.COMMIT;
//        } catch (Exception e) {
//            log.error("本地事务执行失败: {}", orderNo, e);
//            return RocketMQLocalTransactionState.ROLLBACK;
//        }
//    }
//    /**
//     * 检查本地事务状态 - MQ主动回调检查
//     */
//    @Override
//    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
//        // 检查本地事务是否成功
//        String orderNo = msg.getHeaders().get("KEYS").toString();
//        try {
//            Integer status = orderMapper.selectStatusByOrderNo(orderNo);
//            if (status != null && status == OrderStatus.CREATE_PENDING.getCode()) {
//                return RocketMQLocalTransactionState.COMMIT;
//            }
//            return RocketMQLocalTransactionState.ROLLBACK;
//        } catch (Exception e) {
//            return RocketMQLocalTransactionState.UNKNOWN;
//        }
//    }
//}



package com.lm.order.mq.listener;

import com.lm.order.Eumn.OrderStatus;
import com.lm.order.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 订单事务监听器
 * 职责：管理本地数据库事务的提交和回滚
 */
@Slf4j
@RocketMQTransactionListener
public class OrderTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 执行本地事务 - 消息发送后立即回调
     */
    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String orderNo = (String) arg;
        log.info("开始执行本地事务，orderNo: {}", orderNo);

        try {
            // 更新订单状态为"创建中"
            int updateCount = orderMapper.updateStatus(orderNo, OrderStatus.CREATING.getCode());

            if (updateCount == 0) {
                log.error("订单不存在，回滚消息，orderNo: {}", orderNo);
                return RocketMQLocalTransactionState.ROLLBACK;
            }

            log.info("本地事务执行成功，orderNo: {}", orderNo);
            return RocketMQLocalTransactionState.COMMIT;

        } catch (Exception e) {
            log.error("本地事务执行失败，回滚消息，orderNo: {}", orderNo, e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务状态 - MQ主动回调检查
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // 从消息头中获取订单号（更可靠的方式）
        String orderNo = getOrderNoFromMessage(msg);
        log.info("检查本地事务状态，orderNo: {}", orderNo);

        if (orderNo == null) {
            log.warn("无法从消息中获取订单号，回滚");
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        try {
            Integer status = orderMapper.selectStatusByOrderNo(orderNo);

            if (status == null) {
                log.warn("订单不存在，回滚消息，orderNo: {}", orderNo);
                return RocketMQLocalTransactionState.ROLLBACK;
            }

            // 如果订单状态是CREATING，说明本地事务成功
            if (Objects.equals(status, OrderStatus.CREATING.getCode())) {
                log.info("本地事务检查成功，orderNo: {}", orderNo);
                return RocketMQLocalTransactionState.COMMIT;
            }

            log.warn("订单状态异常[{}]，回滚消息，orderNo: {}", status, orderNo);
            return RocketMQLocalTransactionState.ROLLBACK;

        } catch (Exception e) {
            log.error("检查本地事务状态异常，orderNo: {}", orderNo, e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    /**
     * 从消息中获取订单号（多种方式尝试）
     */
    private String getOrderNoFromMessage(Message msg) {
        try {
            // 方式1：从arg参数获取（executeLocalTransaction中使用的）
            Object arg = msg.getHeaders().get("ARG");
            if (arg instanceof String) {
                return (String) arg;
            }

            // 方式2：从KEYS头信息获取
            Object keys = msg.getHeaders().get("KEYS");
            if (keys instanceof String) {
                return (String) keys;
            }

            // 方式3：从rocketmq的KEYS属性获取
            Object rocketmqKeys = msg.getHeaders().get("ROCKETMQ_KEYS");
            if (rocketmqKeys instanceof String) {
                return (String) rocketmqKeys;
            }

            // 方式4：从自定义头信息获取
            Object orderNoHeader = msg.getHeaders().get("orderNo");
            if (orderNoHeader instanceof String) {
                return (String) orderNoHeader;
            }

            log.warn("无法从消息头中获取订单号，headers: {}", msg.getHeaders());
            return null;

        } catch (Exception e) {
            log.error("获取订单号失败", e);
            return null;
        }
    }
}