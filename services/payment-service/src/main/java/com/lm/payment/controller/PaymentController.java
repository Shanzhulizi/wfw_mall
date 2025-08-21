package com.lm.payment.controller;

import com.lm.payment.dto.PaymentInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@RequestMapping("/payment")
@Slf4j
@ResponseBody
@Controller
public class PaymentController {



    @PostMapping("/create")
    PaymentInfoDTO createPayment(PaymentInfoDTO paymentInfoDTO){
        //TODO 假装这里是调用支付API创建支付订单的逻辑
        PaymentInfoDTO paymentInfo = createPaymentAPI(paymentInfoDTO);
           // 这里可以添加实际的支付逻辑
        return paymentInfo; // 返回支付结果
    }


    public PaymentInfoDTO createPaymentAPI(PaymentInfoDTO paymentInfoDTO){
        String payNo = "PAY" + System.currentTimeMillis()+paymentInfoDTO.getOrderNo() ; // 生成一个唯一的支付单号
        paymentInfoDTO.setPayNo(payNo);
        paymentInfoDTO.setCreateTime(LocalDateTime.now());
        paymentInfoDTO.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 设置支付过期时间为30分钟后
        paymentInfoDTO.setStatus(0); // 0待支付
        paymentInfoDTO.setPayUrl("http://example.com/pay?payNo=" + paymentInfoDTO.getPayNo());
        return paymentInfoDTO;
    }
}
