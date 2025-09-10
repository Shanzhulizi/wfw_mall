package com.lm.product.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.utils.StringUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class ProductSkuDTO {
    private Long id;
    private Long spuId;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String attrValueJson; // JSON字符串，如：[{"颜色":"红色"}, {"尺码":"XL"}]
    private Date createTime;
    private Date updateTime;

    // 可以添加一个方法来解析attrValueJson
    public List<SkuAttribute> getAttributes() {
        if (StringUtils.isBlank(attrValueJson)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(attrValueJson, SkuAttribute.class);
        } catch (Exception e) {
//            log.error("解析SKU属性JSON失败: {}");
            return Collections.emptyList();
        }
    }

    // SKU属性内部类
    @Data
    public static class SkuAttribute {
        private String key;
        private String value;
    }
}