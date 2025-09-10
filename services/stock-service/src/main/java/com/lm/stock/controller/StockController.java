package com.lm.stock.controller;

import com.lm.common.R;
import com.lm.order.domain.OrderItem;
import com.lm.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@Slf4j
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;


    /**
     * 扣减库存（订单创建时调用）
     */
    @PostMapping("/deduct")
    public R deductStock(@RequestBody List<OrderItem> orderItems) {
        try {
            stockService.deductStock(orderItems);
            return R.ok("扣减库存成功");
        } catch (Exception e) {
            return R.error("扣减库存失败: " + e.getMessage());
        }
    }

    /**
     * 回滚库存（订单失败/取消时调用）
     */
    @PostMapping("/restore")
    public R restoreStock(@RequestBody List<OrderItem> orderItems) {
        try {
            stockService.restoreStock(orderItems);
            return R.ok("回滚库存成功");
        } catch (Exception e) {
            return R.error("回滚库存失败: " + e.getMessage());
        }
    }
}
