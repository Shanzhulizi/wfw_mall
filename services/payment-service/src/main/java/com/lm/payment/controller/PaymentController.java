package com.lm.payment.controller;

import com.lm.common.R;
import com.lm.payment.dto.PaymentRequest;
import com.lm.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/payment")
@Slf4j
@ResponseBody
@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/pay")
    public R getOrder(@RequestBody PaymentRequest paymentRequest) {
        try {
            boolean success = paymentService.payOrder(paymentRequest.getOrderNo(), paymentRequest.getPayType());
            if (success) {
                return R.ok("支付成功");
            } else {
                return R.error("支付失败，请重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("支付异常：" + e.getMessage());
        }
    }


    // 支付回调（第三方异步回调）
//    @PostMapping("/notify/{provider}")
//    public String notify(@PathVariable("provider") String provider, HttpServletRequest request) {
//        Map<String, String> params = extractParams(request); // 从 request 里取所有回调字段
//        try {
//            paymentService.handleProviderNotify(provider, params);
//            // 多数平台要求返回特定字符串表示已成功接收
//            if ("alipay".equals(provider)) return "success";
//            if ("wechat".equals(provider)) return "<xml><return_code>SUCCESS</return_code></xml>";
//            return "OK";
//        } catch (InvalidSignatureException e) {
//            return "FAIL";
//        } catch (Exception e) {
//            // 如果抛异常，部分平台会继续重试
//            return "FAIL";
//        }
//    }

}
