package com.lm.util;

import com.lm.es.domain.EsSyncRecord;
import com.lm.mapper.EsSyncRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class SyncRecordHelper {

    @Autowired
    private EsSyncRecordMapper syncRecordMapper;

    /**
     * 记录同步成功
     */
    public void recordSuccess(Long spuId, String batchId) {
        try {
            EsSyncRecord record = new EsSyncRecord();
            record.setSpuId(spuId);
            record.setStatus(1);
            record.setErrorMsg("成功");
            record.setRetryCount(0);
            record.setBatchId(batchId);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            syncRecordMapper.insert(record);
        } catch (Exception e) {
            log.warn("记录成功状态失败: {}", e.getMessage());
        }
    }

    /**
     * 记录同步失败
     */
    public void recordFailure(Long spuId, String errorMsg, String batchId) {
        try {
            EsSyncRecord record = new EsSyncRecord();
            record.setSpuId(spuId);
            record.setStatus(0);
            record.setErrorMsg(errorMsg);
            record.setRetryCount(0);
            record.setBatchId(batchId);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            syncRecordMapper.insert(record);
        } catch (Exception e) {
            log.warn("记录失败状态失败: {}", e.getMessage());
        }
    }

    /**
     * 获取需要重试的SPU ID列表
     */
    public List<Long> getSpuIdsNeedRetry() {
        try {
            return syncRecordMapper.selectNeedRetrySpuIds();
        } catch (Exception e) {
            log.warn("获取重试列表失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}