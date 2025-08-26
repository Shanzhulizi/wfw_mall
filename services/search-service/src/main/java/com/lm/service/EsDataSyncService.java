package com.lm.service;

public interface EsDataSyncService {
    void fullSync();

    void incrementalSync();
}
