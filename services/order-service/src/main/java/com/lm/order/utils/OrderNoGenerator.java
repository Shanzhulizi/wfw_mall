package com.lm.order.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderNoGenerator {
    /**
     * 生成唯一的订单号
     * 在 中小业务、并发量不高 的场景下，冲突概率极低，且兼顾了订单号的可读性和简洁性，是一种实用的折中方案
     *
     *
     * 对于高并发场景（如秒杀、电商大促），更推荐使用成熟的全局唯一 ID 方案，例如：
     * 雪花算法（Snowflake）：生成 64 位 Long 型 ID，包含时间戳、机器 ID、序列号，确保分布式环境下唯一。
     */
    //TODO 很明显，在高并发下，如果两个有相同后缀的用户在一家商户在同一时刻购买，会产生冲突，这里暂时使用这个
    public static String generateOrderNo(Long userId, Long merchantId){
        // 订单号格式：yyyyMMddHHmmss + merchantId + userId

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = localDateTime.format(formatter);

        String orderNo = formattedDate + String.format("%04d", merchantId % 10000) + String.format("%04d", userId % 10000);

        return orderNo;
    }
}
