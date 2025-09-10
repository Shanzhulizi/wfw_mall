//package com.lm.order.controller;
//
//import com.lm.common.R;
//import com.lm.order.service.OrderService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/payment")
//@Slf4j
//public class PaymentCallbackController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @PostMapping("/callback")
//    public R paymentCallback(@RequestBody PaymentCallbackDTO callbackDTO) {
//        try {
//            // 1. 验证签名
//            if (!verifySignature(callbackDTO)) {
//                return R.error("签名验证失败");
//            }
//
//            // 2. 处理支付结果
//            if ("SUCCESS".equals(callbackDTO.getTradeStatus())) {
//                // 支付成功
//                orderService.handlePaymentSuccess(
//                        callbackDTO.getOrderNo(),
//                        callbackDTO.getTransactionId(),
//                        callbackDTO.getPayTime()
//                );
//            } else {
//                // 支付失败
//                orderService.handlePaymentFailure(
//                        callbackDTO.getOrderNo(),
//                        callbackDTO.getFailReason()
//                );
//            }
//
//            return R.ok("处理成功");
//        } catch (Exception e) {
//            log.error("支付回调处理异常", e);
//            return R.error("处理失败");
//        }
//    }
//}
