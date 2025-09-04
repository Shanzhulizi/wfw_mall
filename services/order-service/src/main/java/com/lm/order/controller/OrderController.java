package com.lm.order.controller;


import com.lm.common.R;
import com.lm.order.dto.OrderSubmitDTO;
import com.lm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("/submit")
    public R submitOrder(@RequestBody OrderSubmitDTO dto){

        R r = orderService.submitOrder(dto);
        if (r.getCode() != 200) {
            log.error("创建订单失败，错误信息：{}", r.getMsg());
            return R.error(r.getMsg());
        }

        return R.ok("创建订单成功",r.getData());
    }
}
