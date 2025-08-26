package com.lm.config;

import com.lm.service.EsDataSyncService;
import com.lm.service.impl.EsDataSyncServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SyncScheduleConfig {

    @Autowired
    private EsDataSyncService esDataSyncService;
    @Autowired
    private EsDataSyncServiceImpl dataSyncService;


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




    // 每10分钟重试一次失败记录
    @Scheduled(fixedRate = 600000)
    public void scheduledRetry() {
        dataSyncService.retryFailedSync();
    }
}