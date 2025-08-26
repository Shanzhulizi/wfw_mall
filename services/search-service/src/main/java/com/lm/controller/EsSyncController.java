package com.lm.controller;

import com.lm.common.R;
import com.lm.service.EsDataSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/es-sync")
public class EsSyncController {

    @Autowired
    private EsDataSyncService esDataSyncService;

    @PostMapping("/full")
    public R fullSync() {
        try {
            esDataSyncService.fullSync();
            return R.ok("全量同步完成");
        } catch (Exception e) {
            log.error("全量同步异常", e);
            return R.error("全量同步失败: " + e.getMessage());
        }
    }

    @PostMapping("/incremental")
    public R incrementalSync() {
        try {
            esDataSyncService.incrementalSync();
            return R.ok("增量同步完成");
        } catch (Exception e) {
            log.error("增量同步异常", e);
            return R.error("增量同步失败: " + e.getMessage());
        }
    }
}