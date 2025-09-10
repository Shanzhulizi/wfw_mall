package com.lm.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNo;
    private Integer payType;
}
