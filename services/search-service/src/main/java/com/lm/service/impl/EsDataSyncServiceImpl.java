package com.lm.service.impl;

import com.lm.common.R;
import com.lm.feign.ProductFeignClient;
import com.lm.feign.UserFeignClient;
import com.lm.service.EsDataSyncService;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class EsDataSyncServiceImpl implements EsDataSyncService {




    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient merchantFeignClient;

    // 全量同步
    public void fullSync() {
        log.info("开始全量同步商品数据到ES");

        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalCount = 0;

        log.info("同步开始，采用分页批量处理");

        while (hasMore) {
            try {
                log.info("正在获取第{}页数据，每页{}条", page, size);

                // 通过Feign获取商品SPU数据，带分页参数
                R response = productFeignClient.listSpus(null, page, size);

                if (response.isSuccess()) {
                    List<Map<String, Object>> spuList = (List<Map<String, Object>>) response.getData();

                    if (spuList == null || spuList.isEmpty()) {
                        hasMore = false;
                        log.info("没有更多数据，同步完成");
                    } else {
                        // 处理并索引这批数据
                        indexSpuList(spuList);
                        totalCount += spuList.size();
                        log.info("已同步第{}页数据，{}条记录，总计{}条", page, spuList.size(), totalCount);
                        page++;

                        // 添加短暂延迟，避免对数据库和网络造成过大压力
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.warn("同步被中断");
                            break;
                        }
                    }
                } else {
                    log.error("获取第{}页商品数据失败: {}", page, response.getMsg());
                    // 可以选择重试或继续下一页
                    page++;
                    if (page > 100) { // 防止无限循环
                        log.error("重试次数过多，终止同步");
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("同步第{}页数据时发生异常: {}", page, e.getMessage(), e);
                page++;
                if (page > 100) { // 防止无限循环
                    log.error("异常次数过多，终止同步");
                    break;
                }
            }
        }

        log.info("全量同步完成，共同步{}条记录", totalCount);
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