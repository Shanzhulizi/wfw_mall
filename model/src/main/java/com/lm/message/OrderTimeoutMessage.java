package com.lm.message;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderTimeoutMessage implements Serializable {
    private String orderNo;
    private LocalDateTime expectedPayTime;
    private int retryCount = 0;
}
