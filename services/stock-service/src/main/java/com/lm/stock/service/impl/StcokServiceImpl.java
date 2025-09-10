package com.lm.stock.service.impl;

import com.lm.common.R;
import com.lm.order.domain.OrderItem;
import com.lm.stock.mapper.StockMapper;
import com.lm.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StcokServiceImpl implements StockService {


    @Autowired
    private StockMapper stockMapper; // 直接操作 product_sku 表
    @Override
    @Transactional
    public void deductStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            int rows = stockMapper.deductStock(item.getSkuId(), item.getQuantity());
            if (rows == 0) {
                throw new RuntimeException("扣减库存失败，可能库存不足或版本冲突，skuId=" + item.getSkuId());
            }
        }
    }

    @Override
    @Transactional
    public void restoreStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            int rows = stockMapper.restoreStock(item.getSkuId(), item.getQuantity());
            if (rows == 0) {
                throw new RuntimeException("回滚库存失败，skuId=" + item.getSkuId());
            }
        }
    }
}
