package com.lm.stock.service;

public interface StockService {
    void deductDBStock(Long skuId, Integer quantity);
}
