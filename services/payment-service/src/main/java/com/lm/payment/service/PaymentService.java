package com.lm.payment.service;

public interface PaymentService {
    boolean payOrder(String orderNo, Integer payType);

}
