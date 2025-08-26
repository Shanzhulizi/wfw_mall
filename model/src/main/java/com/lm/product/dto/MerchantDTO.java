package com.lm.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantDTO {
    private Long id;
    private String name;           // 商家名称
    private String code;           // 商家编码
    private String contactPerson;  // 联系人
    private String contactPhone;   // 联系电话
    private String address;        // 地址
    private Integer status;        // 状态：0-禁用，1-启用
    private String businessLicense;// 营业执照
    private String description;    // 商家描述
    private Date createTime;
    private Date updateTime;

    // 可选：商家等级、评分等信息
    private Integer level;
    private BigDecimal score;
}