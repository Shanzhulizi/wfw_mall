package com.lm.order.Service;

import java.util.List;

public interface StockLuaService {
    boolean deductStock(List<String> keys, List<Integer> buyNums);
}
