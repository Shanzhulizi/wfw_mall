package com.lm.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDeductMessage {
    private String orderNo;
    private List<StockItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockItem {
        private Long skuId;
        private Integer quantity;
    }
}