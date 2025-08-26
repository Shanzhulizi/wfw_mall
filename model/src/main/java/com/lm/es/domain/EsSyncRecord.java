package com.lm.es.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("es_sync_record")
public class EsSyncRecord {
    private Long id;
    private Long spuId;
    private Integer status; // 0:失败, 1:成功, 2:重试中
    private String errorMsg;
    private Integer retryCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String batchId; // 批次ID，用于区分不同的同步任务
}