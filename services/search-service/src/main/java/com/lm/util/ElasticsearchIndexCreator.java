package com.lm.util;



import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 在搜索微服务中创建ES索引映射
@Component
public class ElasticsearchIndexCreator {

    @Autowired
    private RestHighLevelClient esClient;

    public void createProductIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("product_index");

        // 索引映射配置
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                // SPU字段
                .startObject("id").field("type", "keyword").endObject()
                .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("categoryId").field("type", "keyword").endObject()
                .startObject("brandId").field("type", "keyword").endObject()
                .startObject("merchantId").field("type", "keyword").endObject()
                .startObject("description").field("type", "text").field("analyzer", "ik_smart").endObject()
                .startObject("mainImage").field("type", "keyword").endObject()
                .startObject("status").field("type", "integer").endObject()
                .startObject("isHot").field("type", "integer").endObject()
                .startObject("isNew").field("type", "integer").endObject()
                .startObject("isRecommended").field("type", "integer").endObject()
                .startObject("saleCount").field("type", "integer").endObject()

                // 添加minPrice和maxPrice字段
                .startObject("minPrice")
                .field("type", "scaled_float")
                .field("scaling_factor", 100)
                .endObject()
                .startObject("maxPrice")
                .field("type", "scaled_float")
                .field("scaling_factor", 100)
                .endObject()

                .startObject("createTime").field("type", "date").endObject()
                .startObject("updateTime").field("type", "date").endObject()

                // 分类信息
                .startObject("categoryName").field("type", "keyword").endObject()
                .startObject("brandName").field("type", "keyword").endObject()
                .startObject("shopName").field("type", "keyword").endObject()

                // SKU嵌套文档
                .startObject("skus")
                .field("type", "nested")
                .startObject("properties")
                .startObject("id").field("type", "keyword").endObject()
                .startObject("skuName").field("type", "text").field("analyzer", "ik_max_word").endObject()
                .startObject("price").field("type", "scaled_float").field("scaling_factor", 100).endObject()
                .startObject("stock").field("type", "integer").endObject()
                .startObject("image").field("type", "keyword").endObject()
                .startObject("attrValueJson").field("type", "text").endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        request.mapping(mappingBuilder.toString());
        esClient.indices().create(request, RequestOptions.DEFAULT);
    }
}