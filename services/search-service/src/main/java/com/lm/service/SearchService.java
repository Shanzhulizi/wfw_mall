package com.lm.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.lm.es.dto.ProductSearchItem;
import com.lm.es.dto.ProductSearchRequest;
import com.lm.es.dto.ProductSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



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


    /**
     * 构建布尔查询 - 修复价格筛选逻辑
     */
    private BoolQueryBuilder buildBoolQuery(ProductSearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词搜索
        if (StringUtils.isNotBlank(request.getKeyword())) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("name", request.getKeyword()).boost(2.0f))
                    .should(QueryBuilders.matchQuery("description", request.getKeyword()));
            boolQuery.must(keywordQuery);
        }

        // 价格范围过滤 - 修复逻辑
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            RangeQueryBuilder priceRangeQuery = QueryBuilders.rangeQuery("minPrice");

            if (request.getMinPrice() != null) {
                priceRangeQuery.gte(request.getMinPrice().doubleValue());
            }
            if (request.getMaxPrice() != null) {
                // 处理最大值限制
                double maxPriceValue = request.getMaxPrice().doubleValue();
                if (maxPriceValue < 999999) { // 避免过大的范围
                    priceRangeQuery.lte(maxPriceValue);
                }
                // 如果maxPrice是999999，则不设置上限，或者设置为一个合理的最大值
            }
            boolQuery.filter(priceRangeQuery); // 使用filter而不是must，避免影响评分
        }

        // 添加默认过滤条件（如上架商品）
        boolQuery.filter(QueryBuilders.termQuery("status", 1));

        return boolQuery;
    }

    /**
     * 构建排序 - 修复排序逻辑
     */
    private void buildSort(ProductSearchRequest request, SearchSourceBuilder sourceBuilder) {
        if (StringUtils.isNotBlank(request.getSort())) {
            switch (request.getSort()) {
                case "price_asc":
                    sourceBuilder.sort("minPrice", SortOrder.ASC);
                    break;
                case "price_desc":
                    sourceBuilder.sort("minPrice", SortOrder.DESC);
                    break;
                case "sales_desc":
                    sourceBuilder.sort("saleCount", SortOrder.DESC);
                    break;
                default:
                    // 默认按相关度排序
                    sourceBuilder.sort("_score", SortOrder.DESC);
                    // 添加次要排序条件确保结果稳定
                    sourceBuilder.sort("saleCount", SortOrder.DESC);
            }
        } else {
            // 默认排序：相关度 + 销量
            sourceBuilder.sort("_score", SortOrder.DESC);
            sourceBuilder.sort("saleCount", SortOrder.DESC);
        }
    }

    /**
     * 处理价格范围 - 确保minPrice字段存在
     */
    private void processPriceRange(Map<String, Object> source, ProductSearchItem item) {
        try {
            // 首先检查是否已经有minPrice字段（可能在索引时计算）
            if (source.containsKey("minPrice")) {
                Object minPriceObj = source.get("minPrice");
                if (minPriceObj != null) {
                    item.setMinPrice(new BigDecimal(minPriceObj.toString()));
                }
            }

            // 如果没有minPrice字段，从SKU计算
            if (item.getMinPrice() == null) {
                List<Map<String, Object>> skus = (List<Map<String, Object>>) source.get("skus");
                if (skus != null && !skus.isEmpty()) {
                    BigDecimal minPrice = skus.stream()
                            .map(sku -> {
                                Object priceObj = sku.get("price");
                                return priceObj != null ? new BigDecimal(priceObj.toString()) : BigDecimal.ZERO;
                            })
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    BigDecimal maxPrice = skus.stream()
                            .map(sku -> {
                                Object priceObj = sku.get("price");
                                return priceObj != null ? new BigDecimal(priceObj.toString()) : BigDecimal.ZERO;
                            })
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    item.setMinPrice(minPrice);
                    item.setMaxPrice(maxPrice);
                } else {
                    item.setMinPrice(BigDecimal.ZERO);
                    item.setMaxPrice(BigDecimal.ZERO);
                }
            }
        } catch (Exception e) {
            log.warn("处理价格范围异常", e);
            item.setMinPrice(BigDecimal.ZERO);
            item.setMaxPrice(BigDecimal.ZERO);
        }
    }
}











