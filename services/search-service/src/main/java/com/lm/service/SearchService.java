package com.lm.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.lm.es.dto.ProductSearchItem;
import com.lm.es.dto.ProductSearchRequest;
import com.lm.es.dto.ProductSearchResult;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


//@Service
//@Slf4j
//public class SearchService {
//
//    @Autowired
//    private RestHighLevelClient esClient;
//
//    /**
//     * 商品搜索
//     */
//    public ProductSearchResult searchProducts(ProductSearchRequest request) {
//        try {
//            SearchRequest searchRequest = new SearchRequest("product_index");
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//
//            // 构建查询条件
//            BoolQueryBuilder boolQuery = buildBoolQuery(request);
//            sourceBuilder.query(boolQuery);
//
//            // 设置排序
//            buildSort(request, sourceBuilder);
//
//            // 设置分页
//            sourceBuilder.from(request.getFrom());
//            sourceBuilder.size(request.getSize());
//
//            // 设置高亮
//            buildHighlight(sourceBuilder);
//
//            searchRequest.source(sourceBuilder);
//
//            // 执行搜索
//            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
//
//            // 解析结果
//            return parseSearchResult(response, request);
//
//        } catch (Exception e) {
//            log.error("商品搜索异常", e);
//            throw new RuntimeException("搜索失败", e);
//        }
//    }
//
//    /**
//     * 构建布尔查询
//     */
//    private BoolQueryBuilder buildBoolQuery(ProductSearchRequest request) {
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//
//        // 关键词搜索（搜索名称、描述）
//        if (StringUtils.isNotBlank(request.getKeyword())) {
//            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery()
//                    .should(QueryBuilders.matchQuery("name", request.getKeyword()).boost(2.0f))
//                    .should(QueryBuilders.matchQuery("description", request.getKeyword()));
//            boolQuery.must(keywordQuery);
//        }
//
//        // 分类过滤
//        if (request.getCategoryId() != null) {
//            boolQuery.must(QueryBuilders.termQuery("categoryId", request.getCategoryId()));
//        }
//
//        // 品牌过滤
//        if (request.getBrandId() != null) {
//            boolQuery.must(QueryBuilders.termQuery("brandId", request.getBrandId()));
//        }
//
//        // 商家过滤
//        if (request.getMerchantId() != null) {
//            boolQuery.must(QueryBuilders.termQuery("merchantId", request.getMerchantId()));
//        }
//
//        // 价格范围过滤
//        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
//            RangeQueryBuilder priceRangeQuery = QueryBuilders.rangeQuery("skus.price");
//            if (request.getMinPrice() != null) {
//                priceRangeQuery.gte(request.getMinPrice());
//            }
//            if (request.getMaxPrice() != null) {
//                priceRangeQuery.lte(request.getMaxPrice());
//            }
//            boolQuery.must(priceRangeQuery);
//        }
//
//        // 状态过滤
//        if (request.getStatus() != null) {
//            boolQuery.must(QueryBuilders.termQuery("status", request.getStatus()));
//        }
//
//        // 热销过滤
//        if (request.getIsHot() != null) {
//            boolQuery.must(QueryBuilders.termQuery("isHot", request.getIsHot()));
//        }
//
//        // 新品过滤
//        if (request.getIsNew() != null) {
//            boolQuery.must(QueryBuilders.termQuery("isNew", request.getIsNew()));
//        }
//
//        // 推荐过滤
//        if (request.getIsRecommended() != null) {
//            boolQuery.must(QueryBuilders.termQuery("isRecommended", request.getIsRecommended()));
//        }
//
//        // 只查询上架商品
//        boolQuery.must(QueryBuilders.termQuery("status", 1));
//
//        return boolQuery;
//    }
//
//    /**
//     * 构建排序
//     */
//    private void buildSort(ProductSearchRequest request, SearchSourceBuilder sourceBuilder) {
//        if ("saleCount".equals(request.getSortField())) {
//            sourceBuilder.sort("saleCount",
//                    "desc".equalsIgnoreCase(request.getSortOrder()) ?
//                            SortOrder.DESC : SortOrder.ASC);
//        } else if ("price".equals(request.getSortField())) {
//            sourceBuilder.sort("skus.price",
//                    "desc".equalsIgnoreCase(request.getSortOrder()) ?
//                            SortOrder.DESC : SortOrder.ASC);
//        } else if ("createTime".equals(request.getSortField())) {
//            sourceBuilder.sort("createTime",
//                    "desc".equalsIgnoreCase(request.getSortOrder()) ?
//                            SortOrder.DESC : SortOrder.ASC);
//        } else {
//            // 默认按相关性评分排序
//            sourceBuilder.sort("_score", SortOrder.DESC);
//        }
//    }
//
//    /**
//     * 构建高亮
//     */
//    private void buildHighlight(SearchSourceBuilder sourceBuilder) {
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("name");
//        highlightBuilder.field("description");
//        highlightBuilder.preTags("<em>");
//        highlightBuilder.postTags("</em>");
//        sourceBuilder.highlighter(highlightBuilder);
//    }
//
//    /**
//     * 解析搜索结果
//     */
//    private ProductSearchResult parseSearchResult(SearchResponse response, ProductSearchRequest request) {
//        List<ProductSearchItem> products = new ArrayList<>();
//
//        for (SearchHit hit : response.getHits().getHits()) {
//            Map<String, Object> source = hit.getSourceAsMap();
//            ProductSearchItem item = convertToSearchItem(source, hit);
//            products.add(item);
//        }
//
//        long total = response.getHits().getTotalHits().value;
//        return new ProductSearchResult(products, total, request.getPage(), request.getSize());
//    }
//
//    /**
//     * 转换为搜索项
//     */
//    private ProductSearchItem convertToSearchItem(Map<String, Object> source, SearchHit hit) {
//        ProductSearchItem item = new ProductSearchItem();
//
//        item.setId(convertToLong(source.get("id")));
//        item.setName((String) source.get("name"));
//        item.setDescription((String) source.get("description"));
//        item.setMainImage((String) source.get("mainImage"));
//        item.setSaleCount((Integer) source.get("saleCount"));
//        item.setCategoryName((String) source.get("categoryName"));
//        item.setBrandName((String) source.get("brandName"));
//        item.setShopName((String) source.get("shopName"));
//        item.setStatus((Integer) source.get("status"));
//        item.setIsHot((Integer) source.get("isHot"));
//        item.setIsNew((Integer) source.get("isNew"));
//        item.setIsRecommended((Integer) source.get("isRecommended"));
//
//        // 处理价格范围（从SKU中计算）
//        processPriceRange(source, item);
//
//        // 处理高亮
//        processHighlight(hit, item);
//
//        return item;
//    }
//
//    /**
//     * 处理价格范围
//     */
//    private void processPriceRange(Map<String, Object> source, ProductSearchItem item) {
//        try {
//            List<Map<String, Object>> skus = (List<Map<String, Object>>) source.get("skus");
//            if (skus != null && !skus.isEmpty()) {
//                BigDecimal minPrice = skus.stream()
//                        .map(sku -> new BigDecimal(sku.get("price").toString()))
//                        .min(BigDecimal::compareTo)
//                        .orElse(BigDecimal.ZERO);
//
//                BigDecimal maxPrice = skus.stream()
//                        .map(sku -> new BigDecimal(sku.get("price").toString()))
//                        .max(BigDecimal::compareTo)
//                        .orElse(BigDecimal.ZERO);
//
//                item.setMinPrice(minPrice);
//                item.setMaxPrice(maxPrice);
//            }
//        } catch (Exception e) {
//            log.warn("处理价格范围异常", e);
//        }
//    }
//
//    /**
//     * 处理高亮
//     */
//    private void processHighlight(SearchHit hit, ProductSearchItem item) {
//        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//
//        if (highlightFields.containsKey("name")) {
//            String highlightedName = highlightFields.get("name").fragments()[0].string();
//            item.setName(highlightedName);
//        }
//
//        if (highlightFields.containsKey("description")) {
//            String highlightedDesc = highlightFields.get("description").fragments()[0].string();
//            item.setDescription(highlightedDesc);
//        }
//    }
//
//    private Long convertToLong(Object value) {
//        if (value == null) return null;
//        if (value instanceof Long) return (Long) value;
//        if (value instanceof Integer) return ((Integer) value).longValue();
//        return null;
//    }
//}



@Service
@Slf4j
public class SearchService {

    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 商品搜索
     */
    public ProductSearchResult searchProducts(ProductSearchRequest request) {
        try {
            SearchRequest searchRequest = new SearchRequest("product_index");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 构建查询条件
            BoolQueryBuilder boolQuery = buildBoolQuery(request);
            sourceBuilder.query(boolQuery);

            // 设置排序
            buildSort(request, sourceBuilder);

            // 设置分页
            sourceBuilder.from(request.getFrom());
            sourceBuilder.size(request.getSize());

            // 设置高亮
            buildHighlight(sourceBuilder);

            searchRequest.source(sourceBuilder);

            log.debug("ES查询DSL: {}", sourceBuilder.toString());

            // 执行搜索
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);

            // 解析结果
            return parseSearchResult(response, request);

        } catch (Exception e) {
            log.error("商品搜索异常", e);
            throw new RuntimeException("搜索失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建布尔查询
     */
    private BoolQueryBuilder buildBoolQuery(ProductSearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词搜索（搜索名称、描述）
        if (StringUtils.isNotBlank(request.getKeyword())) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", request.getKeyword()).boost(2.0f))
                    .should(QueryBuilders.matchQuery("description", request.getKeyword()));
            boolQuery.must(keywordQuery);
        }

        // 分类过滤
        if (request.getCategoryId() != null) {
            boolQuery.must(QueryBuilders.termQuery("categoryId", request.getCategoryId()));
        }

        // 品牌过滤
        if (request.getBrandId() != null) {
            boolQuery.must(QueryBuilders.termQuery("brandId", request.getBrandId()));
        }

        // 商家过滤
        if (request.getMerchantId() != null) {
            boolQuery.must(QueryBuilders.termQuery("merchantId", request.getMerchantId()));
        }

        // 价格范围过滤 - 修改为使用inner_hits方式
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            // 先构建价格范围查询
            BoolQueryBuilder priceFilterQuery = QueryBuilders.boolQuery();

            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                RangeQueryBuilder priceRangeQuery = QueryBuilders.rangeQuery("skus.price");
                if (request.getMinPrice() != null) {
                    priceRangeQuery.gte(request.getMinPrice());
                }
                if (request.getMaxPrice() != null) {
                    priceRangeQuery.lte(request.getMaxPrice());
                }

                // 使用nested查询
                NestedQueryBuilder nestedPriceQuery = QueryBuilders.nestedQuery("skus", priceRangeQuery, ScoreMode.None);
                boolQuery.must(nestedPriceQuery);
            }
        }

        // 状态过滤
        if (request.getStatus() != null) {
            boolQuery.must(QueryBuilders.termQuery("status", request.getStatus()));
        } else {
            // 默认只查询上架商品
            boolQuery.must(QueryBuilders.termQuery("status", 1));
        }

        // 热销过滤
        if (request.getIsHot() != null) {
            boolQuery.must(QueryBuilders.termQuery("isHot", request.getIsHot()));
        }

        // 新品过滤
        if (request.getIsNew() != null) {
            boolQuery.must(QueryBuilders.termQuery("isNew", request.getIsNew()));
        }

        // 推荐过滤
        if (request.getIsRecommended() != null) {
            boolQuery.must(QueryBuilders.termQuery("isRecommended", request.getIsRecommended()));
        }

        return boolQuery;
    }

    /**
     * 构建排序 - 简化排序逻辑，避免嵌套排序问题
     */
    /**
     * 构建排序
     */
    private void buildSort(ProductSearchRequest request, SearchSourceBuilder sourceBuilder) {
        if (StringUtils.isNotBlank(request.getSortField())) {
            String sortField = request.getSortField();
            SortOrder sortOrder = "desc".equalsIgnoreCase(request.getSortOrder()) ?
                    SortOrder.DESC : SortOrder.ASC;

            if ("saleCount".equals(sortField)) {
                sourceBuilder.sort("saleCount", sortOrder);
            } else if ("price".equals(sortField)) {
                // 使用minPrice字段进行排序（需要在索引中预先计算或使用脚本）
                sourceBuilder.sort("minPrice", sortOrder);
            } else if ("minPrice".equals(sortField)) {
                sourceBuilder.sort("minPrice", sortOrder);
            } else if ("createTime".equals(sortField)) {
                sourceBuilder.sort("createTime", sortOrder);
            } else if ("updateTime".equals(sortField)) {
                sourceBuilder.sort("updateTime", sortOrder);
            } else {
                // 默认按相关性评分排序
                sourceBuilder.sort("_score", SortOrder.DESC);
            }
        } else {
            // 默认按相关性评分排序
            sourceBuilder.sort("_score", SortOrder.DESC);
        }
    }

    /**
     * 构建高亮
     */
    private void buildHighlight(SearchSourceBuilder sourceBuilder) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.field("description");
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");
        sourceBuilder.highlighter(highlightBuilder);
    }

    /**
     * 解析搜索结果
     */
    private ProductSearchResult parseSearchResult(SearchResponse response, ProductSearchRequest request) {
        List<ProductSearchItem> products = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            ProductSearchItem item = convertToSearchItem(source, hit);
            products.add(item);
        }

        long total = response.getHits().getTotalHits().value;
        return new ProductSearchResult(products, total, request.getPage(), request.getSize());
    }

    /**
     * 转换为搜索项
     */
    private ProductSearchItem convertToSearchItem(Map<String, Object> source, SearchHit hit) {
        ProductSearchItem item = new ProductSearchItem();

        item.setId(convertToLong(source.get("id")));
        item.setName((String) source.get("name"));
        item.setDescription((String) source.get("description"));
        item.setMainImage((String) source.get("mainImage"));
        item.setSaleCount((Integer) source.get("saleCount"));
        item.setCategoryName((String) source.get("categoryName"));
        item.setBrandName((String) source.get("brandName"));
        item.setShopName((String) source.get("shopName"));
        item.setStatus((Integer) source.get("status"));
        item.setIsHot((Integer) source.get("isHot"));
        item.setIsNew((Integer) source.get("isNew"));
        item.setIsRecommended((Integer) source.get("isRecommended"));

        // 处理价格范围（从SKU中计算）
        processPriceRange(source, item);

        // 处理SKU列表
        processSkus(source, item);

        // 处理高亮
        processHighlight(hit, item);

        return item;
    }

    /**
     * 处理价格范围
     */
    private void processPriceRange(Map<String, Object> source, ProductSearchItem item) {
        try {
            List<Map<String, Object>> skus = (List<Map<String, Object>>) source.get("skus");
            if (skus != null && !skus.isEmpty()) {
                BigDecimal minPrice = skus.stream()
                        .map(sku -> new BigDecimal(sku.get("price").toString()))
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                BigDecimal maxPrice = skus.stream()
                        .map(sku -> new BigDecimal(sku.get("price").toString()))
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

                item.setMinPrice(minPrice);
                item.setMaxPrice(maxPrice);
            }
        } catch (Exception e) {
            log.warn("处理价格范围异常", e);
            item.setMinPrice(BigDecimal.ZERO);
            item.setMaxPrice(BigDecimal.ZERO);
        }
    }

    /**
     * 处理SKU列表
     */
    /**
     * 处理SKU列表
     */
    private void processSkus(Map<String, Object> source, ProductSearchItem item) {
        try {
            List<Map<String, Object>> skuMaps = (List<Map<String, Object>>) source.get("skus");
            if (skuMaps != null && !skuMaps.isEmpty()) {
                List<ProductSearchItem.SkuItem> skus = skuMaps.stream().map(skuMap -> {
                    ProductSearchItem.SkuItem sku = new ProductSearchItem.SkuItem();

                    // 处理id字段 - 支持多种类型转换
                    Object idObj = skuMap.get("id");
                    if (idObj != null) {
                        if (idObj instanceof String) {
                            sku.setId((String) idObj);
                        } else if (idObj instanceof Integer) {
                            sku.setId(String.valueOf(idObj));
                        } else if (idObj instanceof Long) {
                            sku.setId(String.valueOf(idObj));
                        } else {
                            sku.setId(idObj.toString());
                        }
                    }

                    sku.setSkuName((String) skuMap.get("skuName"));

                    // 处理price字段
                    Object priceObj = skuMap.get("price");
                    if (priceObj != null) {
                        if (priceObj instanceof Number) {
                            sku.setPrice(new BigDecimal(priceObj.toString()));
                        } else if (priceObj instanceof String) {
                            sku.setPrice(new BigDecimal((String) priceObj));
                        }
                    } else {
                        sku.setPrice(BigDecimal.ZERO);
                    }

                    // 处理stock字段
                    Object stockObj = skuMap.get("stock");
                    if (stockObj != null) {
                        if (stockObj instanceof Integer) {
                            sku.setStock((Integer) stockObj);
                        } else if (stockObj instanceof String) {
                            sku.setStock(Integer.parseInt((String) stockObj));
                        } else if (stockObj instanceof Long) {
                            sku.setStock(((Long) stockObj).intValue());
                        }
                    } else {
                        sku.setStock(0);
                    }

                    sku.setImage((String) skuMap.get("image"));
                    sku.setAttrValueJson((String) skuMap.get("attrValueJson"));
                    return sku;
                }).collect(Collectors.toList());
                item.setSkus(skus);
            }
        } catch (Exception e) {
            log.warn("处理SKU列表异常", e);
        }
    }

    /**
     * 处理高亮
     */
    private void processHighlight(SearchHit hit, ProductSearchItem item) {
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();

        if (highlightFields.containsKey("name")) {
            String highlightedName = highlightFields.get("name").fragments()[0].string();
            item.setHighlightedName(highlightedName);
        }

        if (highlightFields.containsKey("description")) {
            String highlightedDesc = highlightFields.get("description").fragments()[0].string();
            item.setHighlightedDescription(highlightedDesc);
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
}











