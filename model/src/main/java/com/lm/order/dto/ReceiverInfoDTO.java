package com.lm.order.dto;

import lombok.Data;

@Data
public class ReceiverInfoDTO {
    private Long receiverInfoId;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String area;
    private String detail_address;

}
