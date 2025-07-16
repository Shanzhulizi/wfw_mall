package com.lm.stock.service.impl;

import com.lm.stock.mapper.StockMapper;
import com.lm.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StcokServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Override
    @Transactional
    public void deductDBStock(Long skuId, Integer quantity) {
        int rows = stockMapper.deductStock(skuId, quantity);
        if (rows == 0) {
            throw new RuntimeException("数据库扣库存失败：库存不足");
        }
    }
}
