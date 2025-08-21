package com.lm.user.domain;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Merchant {

    /** 主键ID */
    private Long id;

    /** 店铺名称 */
    private String shopName;

    private String password; // 商家登录密码，使用BCrypt加

    private String phone; // 商家手机号，唯一

    /** 营业执照 */
    private String businessLicense; //我不知道这玩意怎么审，所以，就当是管理员给批的时候生成的吧

    /** 状态：0 待审核 1 正常营业 2 已封禁 */
    private Integer status;

    /** 店铺描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;
}