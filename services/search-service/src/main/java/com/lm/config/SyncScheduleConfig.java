package com.lm.config;

import com.lm.service.EsDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SyncScheduleConfig {

    @Autowired
    private EsDataSyncService esDataSyncService;

    // 每天凌晨2点执行全量同步
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledFullSync() {
        esDataSyncService.fullSync();
    }

    // 每5分钟执行一次增量同步
    @Scheduled(cron = "0 */40000 * * * ?")
    public void scheduledIncrementalSync() {
        esDataSyncService.incrementalSync();
    }
}