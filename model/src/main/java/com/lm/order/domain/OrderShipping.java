package com.lm.order.domain;

import lombok.Data;

@Data
public class OrderShipping {

    private Long id;
    private Long orderId;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String area;
    private String detailAddress;

}
