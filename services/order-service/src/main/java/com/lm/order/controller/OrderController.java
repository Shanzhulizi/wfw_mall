package com.lm.order.controller;


import com.lm.common.R;
import com.lm.order.service.OrderService;
import com.lm.order.dto.OrderSubmitDTO;
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

/**
 * |→ 调用【商品服务】校验价格和库存
 * |→ 调用【用户服务】获取用户信息(顺便获取地址)
 * |→ 调用【优惠券服务】校验并锁定优惠券
 * |→ Redis 扣减库存（原子 Lua 脚本）
 * |→ 异步发送下单消息到 MQ（下单队列）
 *        ↓
 * 【订单服务消费者】消费消息创建订单 + 插入数据库
 *        ↓
 * 返回订单ID，前端跳转支付页
 *        ↓
 * 用户付款后【支付服务】回调
 *        ↓
 * 更新订单状态 → 通知发货 → 推送消息等
 */
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
