package com.lm.order.controller;


import com.lm.common.R;
import com.lm.order.dto.OrderSubmitDTO;
import com.lm.order.dto.OrderSubmitTestDTO;
import com.lm.order.service.OrderService;
import com.lm.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("/submit")
    public R submitOrder(@RequestBody OrderSubmitDTO dto) {
        try {
            OrderVO orderVo = orderService.submitOrder(dto);
            return R.ok("创建订单成功", orderVo);
        } catch (Exception e) {
            log.error("创建订单失败，错误信息：{}", e.getMessage());
            return R.error("下单失败：" + e.getMessage());
        }
    }


    @PostMapping("/submit/test")
    public R submitOrderTest(@RequestBody OrderSubmitTestDTO dto) {
        try {
            OrderVO orderVo = orderService.submitOrderTest(dto);
            return R.ok("创建订单成功", orderVo);
        } catch (Exception e) {
            log.error("创建订单失败，错误信息：{}", e.getMessage());
            return R.error("下单失败：" + e.getMessage());
        }
    }



    @GetMapping("/detail")
    public OrderVO getOrder(@RequestParam("orderNo") String orderNo) {
        OrderVO orderVo = null;
        try {
            orderVo = orderService.getOrderDetail(orderNo);
            return orderVo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/updatePaid")
    void updateOrderPaid(@RequestParam("orderNo") String orderNo){
        try {
            orderService.updateOrderPaid(orderNo);
        } catch (Exception e) {
            log.error("更新订单支付状态失败，错误信息：{}", e.getMessage());
        }


    }

}
