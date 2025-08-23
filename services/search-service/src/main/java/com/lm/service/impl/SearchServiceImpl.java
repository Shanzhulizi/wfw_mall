package com.lm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lm.es.ESProduct;
import com.lm.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {


    @Autowired
    private RestHighLevelClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String INDEX = "product";


    @Override
    public void bulkInsert(List<ESProduct> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        BulkRequest bulkRequest = new BulkRequest();
        ObjectMapper objectMapper = new ObjectMapper();

        for (ESProduct product : products) {
            try {
                bulkRequest.add(new IndexRequest("product") // ES 索引名
                        .id(product.getId().toString())    // 指定文档 id，避免重复导入
                        .source(objectMapper.writeValueAsString(product), XContentType.JSON));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化商品失败，ID=" + product.getId(), e);
            }
        }

        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                // 打印出错误信息，方便排查
                log.error("批量导入 ES 失败: {}", response.buildFailureMessage());
            } else {
                log.info("批量导入 ES 成功，共 {} 条", products.size());
            }
        } catch (IOException e) {
            throw new RuntimeException("批量导入 ES 出现 IO 异常", e);
        }
    }


    // 搜索
    public List<ESProduct> search(String keyword, String brand, String category,
                                  Double minPrice, Double maxPrice, int page, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.hasText(keyword)) {
            boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "name", "description"));
        }
        if (StringUtils.hasText(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        if (StringUtils.hasText(category)) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }
        if (minPrice != null && maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

        builder.query(boolQuery);
        builder.from((page - 1) * size);
        builder.size(size);

        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        List<ESProduct> result = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            result.add(objectMapper.readValue(hit.getSourceAsString(), ESProduct.class));
        }
        return result;
    }
}
