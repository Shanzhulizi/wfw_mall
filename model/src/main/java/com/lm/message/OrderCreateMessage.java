package com.lm.message;

import com.lm.order.dto.OrderSubmitDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateMessage implements Serializable {
    private String messageId;
    private String orderNo;
    private Long userId;
    private OrderSubmitDTO orderSubmitDTO;
    private LocalDateTime createTime;

    private List<Long> lockedCouponIds;
    private Integer retryCount = 0;
}