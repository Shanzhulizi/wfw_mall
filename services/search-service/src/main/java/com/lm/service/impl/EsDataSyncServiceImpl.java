package com.lm.service.impl;

import com.lm.common.R;
import com.lm.feign.ProductFeignClient;
import com.lm.feign.UserFeignClient;
import com.lm.service.EsDataSyncService;
import com.lm.util.SyncRecordHelper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class EsDataSyncServiceImpl implements EsDataSyncService {




    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient merchantFeignClient;

    @Autowired
    private SyncRecordHelper syncRecordHelper;

    private String currentBatchId;
    // 全量同步
    public void fullSync() {
        String batchId = UUID.randomUUID().toString().substring(0, 8);
        log.info("开始全量同步商品数据到ES，批次ID: {}", batchId);

        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalCount = 0;

        while (hasMore) {
            try {
                R response = productFeignClient.listSpus(null, page, size);

                if (response.isSuccess()) {
                    List<Map<String, Object>> spuList = (List<Map<String, Object>>) response.getData();

                    if (spuList == null || spuList.isEmpty()) {
                        hasMore = false;
                    } else {
                        // 修改这里：使用带记录的方法
                        indexSpuListWithRecord(spuList, batchId);
                        totalCount += spuList.size();
                        page++;
                        Thread.sleep(50);
                    }
                } else {
                    log.error("获取第{}页商品数据失败: {}", page, response.getMsg());
                    page++;
                }
            } catch (Exception e) {
                log.error("同步第{}页数据时发生异常: {}", page, e.getMessage());
                page++;
            }
        }

        log.info("全量同步完成，共同步{}条记录，批次ID: {}", totalCount, batchId);
    }

    // 增量同步
    public void incrementalSync() {
        log.info("开始增量同步商品数据到ES");

        // 获取上次同步时间（可从数据库或Redis中获取）
        long lastSyncTime = getLastSyncTime();

        // 通过Feign获取更新的商品SPU数据
        R response = productFeignClient.listSpus(lastSyncTime);

        if (response.isSuccess()) {
            List<Map<String, Object>> spuList = (List<Map<String, Object>>) response.getData();

            if (!spuList.isEmpty()) {
                // 处理并索引这批数据
                indexSpuList(spuList);
                log.info("增量同步完成，更新了{}条记录", spuList.size());

                // 更新最后同步时间
                updateLastSyncTime(System.currentTimeMillis());
            } else {
                log.info("没有需要更新的数据");
            }
        } else {
            log.error("获取增量商品数据失败: {}", response.getMsg());
        }
    }

    // 索引SPU列表到ES
    private void indexSpuList(List<Map<String, Object>> spuList) {
        BulkRequest bulkRequest = new BulkRequest();

        for (Map<String, Object> spu : spuList) {
            // 构建ES文档
            Map<String, Object> esDocument = buildEsDocument(spu);

            // 创建索引请求
            IndexRequest indexRequest = new IndexRequest("product_index")
                    .id(spu.get("id").toString())
                    .source(esDocument);

            bulkRequest.add(indexRequest);
        }

        // 执行批量操作
        try {
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            if (bulkResponse.hasFailures()) {
                log.error("批量索引失败: {}", bulkResponse.buildFailureMessage());
            }
        } catch (IOException e) {
            log.error("ES索引操作异常", e);
        }
    }

    // 构建ES文档
// 构建ES文档 - 简化版本
    private Map<String, Object> buildEsDocument(Map<String, Object> spu) {
        Map<String, Object> document = new HashMap<>();

        // 复制所有SPU字段
        document.putAll(spu);

        // 获取分类信息
        Number categoryId = (Number) spu.get("categoryId");
        if (categoryId != null) {
            R categoryResponse = productFeignClient.getCategoryById(categoryId.longValue());
            if (categoryResponse.isSuccess() && categoryResponse.getData() != null) {
                Map<String, Object> category = (Map<String, Object>) categoryResponse.getData();
                document.put("categoryName", category.get("name"));
            }
        }

        // 获取品牌信息
        Number brandId = (Number) spu.get("brandId");
        if (brandId != null) {
            R brandResponse = productFeignClient.getBrandById(brandId.longValue());
            if (brandResponse.isSuccess() && brandResponse.getData() != null) {
                Map<String, Object> brand = (Map<String, Object>) brandResponse.getData();
                document.put("brandName", brand.get("name"));
            }
        }

        // 获取商家信息
        Number merchantId = (Number) spu.get("merchantId");
        if (merchantId != null) {
            R merchantResponse = merchantFeignClient.getMerchantById(merchantId.longValue());
            if (merchantResponse.isSuccess() && merchantResponse.getData() != null) {
                Map<String, Object> merchant = (Map<String, Object>) merchantResponse.getData();
                document.put("shopName", merchant.get("shopName"));
            }
        }

        // 获取SKU信息
        Number spuId = (Number) spu.get("id");
        if (spuId != null) {
            R skuResponse = productFeignClient.getSkusBySpuId(spuId.longValue());
            if (skuResponse.isSuccess() && skuResponse.getData() != null) {
                List<Map<String, Object>> skus = (List<Map<String, Object>>) skuResponse.getData();
                document.put("skus", skus);
            }
        }

        return document;
    }
    // 新增：带记录的索引方法
    private void indexSpuListWithRecord(List<Map<String, Object>> spuList, String batchId) {
        BulkRequest bulkRequest = new BulkRequest();

        for (Map<String, Object> spu : spuList) {
            Long spuId = convertToLong(spu.get("id"));

            try {
                Map<String, Object> esDocument = buildEsDocument(spu);
                IndexRequest indexRequest = new IndexRequest("product_index")
                        .id(spuId.toString())
                        .source(esDocument);
                bulkRequest.add(indexRequest);

            } catch (Exception e) {
                // 记录构建文档失败
                syncRecordHelper.recordFailure(spuId, "构建文档失败: " + e.getMessage(), batchId);
                log.warn("SPU {} 构建文档失败: {}", spuId, e.getMessage());
            }
        }

        // 执行批量操作
        try {
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            // 处理批量结果
            if (bulkResponse.hasFailures()) {
                for (BulkItemResponse item : bulkResponse.getItems()) {
                    if (item.isFailed()) {
                        Long failedSpuId = Long.parseLong(item.getId());
                        syncRecordHelper.recordFailure(failedSpuId,
                                "ES索引失败: " + item.getFailureMessage(), batchId);
                    }
                }
            }

        } catch (IOException e) {
            log.error("ES索引操作异常", e);
            // 记录整批失败
            for (Map<String, Object> spu : spuList) {
                Long spuId = convertToLong(spu.get("id"));
                syncRecordHelper.recordFailure(spuId, "ES连接失败: " + e.getMessage(), batchId);
            }
        }
    }
    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 新增：重试方法
    public void retryFailedSync() {
        log.info("开始重试失败的同步记录");

        List<Long> spuIds = syncRecordHelper.getSpuIdsNeedRetry();
        if (spuIds.isEmpty()) {
            log.info("没有需要重试的记录");
            return;
        }

        log.info("找到{}条需要重试的记录", spuIds.size());

        for (Long spuId : spuIds) {
            try {
                // 获取单个SPU数据
                R response = productFeignClient.getSpuById(spuId);
                if (response.isSuccess()) {
                    Map<String, Object> spu = (Map<String, Object>) response.getData();
                    Map<String, Object> esDocument = buildEsDocument(spu);

                    // 索引到ES
                    IndexRequest request = new IndexRequest("product_index")
                            .id(spuId.toString())
                            .source(esDocument);
                    esClient.index(request, RequestOptions.DEFAULT);

                    log.info("SPU {} 重试成功", spuId);
                }
            } catch (Exception e) {
                log.warn("SPU {} 重试失败: {}", spuId, e.getMessage());
            }

            // 添加延迟避免压力过大
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("重试完成");
    }
    // 获取最后同步时间（示例实现）
    private long getLastSyncTime() {
        // 实际项目中可以从数据库或Redis中获取
        return 0L; // 返回0表示全量同步
    }

    // 更新最后同步时间（示例实现）
    private void updateLastSyncTime(long time) {
        // 实际项目中可以保存到数据库或Redis中
    }
}