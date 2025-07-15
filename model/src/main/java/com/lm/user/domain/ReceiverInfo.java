package com.lm.user.domain;

import lombok.Data;

@Data
public class ReceiverInfo {


    private Long id; // ID
    private Long userId; // 用户ID
    private String receiverName; // 收货人姓名
    private String phone; // 收货人电话
    private String province; // 省份
    private String city; // 城市
    private String area; // 区县
    private String street; // 街道地址
    private String detailAddress; // 详细地址
    private Boolean isDefault; // 是否默认地址

}
