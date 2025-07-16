package com.lm.order.service;

import java.util.List;

public interface StockLuaService {
    boolean deductStock(List<String> keys, List<Integer> buyNums);
}
