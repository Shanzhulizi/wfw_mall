package com.lm.service.impl;

import com.lm.es.domain.ESProduct;
import com.lm.es.dto.SearchRequestDTO;
import com.lm.es.vo.SearchResultVO;
import com.lm.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

//    @Autowired
//    private ElasticsearchRestTemplate elasticsearchRestTemplate;
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    // Redis键定义
//    private static final String HOT_KEYWORDS_KEY = "search:hot:keywords";
//    private static final String USER_SEARCH_HISTORY_PREFIX = "search:history:user:";
//    private static final String SEARCH_SUGGESTION_KEY = "search:suggestion:";
//    private static final int MAX_HISTORY_SIZE = 10;
//    private static final int HOT_KEYWORDS_SIZE = 10;
//
//    @Override
//    public List<String> getSuggestions(String keyword) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            return List.of();
//        }
//
//        String normalizedKeyword = keyword.trim().toLowerCase();
//
//        try {
//            // 先尝试从Redis缓存中获取
//            List<String> cachedSuggestions = getSuggestionsFromRedis(normalizedKeyword);
//            if (!cachedSuggestions.isEmpty()) {
//                return cachedSuggestions;
//            }
//
//            // Redis中没有，从ES获取
//            List<String> suggestions = getSuggestionsFromES(normalizedKeyword);
//
//            // 将结果缓存到Redis
//            cacheSuggestionsToRedis(normalizedKeyword, suggestions);
//
//            return suggestions;
//
//        } catch (Exception e) {
//            log.error("获取搜索建议失败, keyword: {}", normalizedKeyword, e);
//            return getFallbackSuggestions(normalizedKeyword);
//        }
//    }
//
//    /**
//     * 从Redis获取搜索建议
//     */
//    private List<String> getSuggestionsFromRedis(String keyword) {
//        try {
//            String key = SEARCH_SUGGESTION_KEY + keyword;
//            Object suggestions = redisTemplate.opsForValue().get(key);
//            if (suggestions instanceof List) {
//                return (List<String>) suggestions;
//            }
//        } catch (Exception e) {
//            log.warn("从Redis获取搜索建议失败", e);
//        }
//        return List.of();
//    }
//
//    /**
//     * 缓存搜索建议到Redis
//     */
//    private void cacheSuggestionsToRedis(String keyword, List<String> suggestions) {
//        try {
//            String key = SEARCH_SUGGESTION_KEY + keyword;
//            redisTemplate.opsForValue().set(key, suggestions, 5, TimeUnit.MINUTES); // 缓存5分钟
//        } catch (Exception e) {
//            log.warn("缓存搜索建议到Redis失败", e);
//        }
//    }
//
//    /**
//     * 从ES获取搜索建议
//     */
//    private List<String> getSuggestionsFromES(String keyword) {
//        try {
//            // 使用Completion Suggester（性能更好）
//            CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders
//                    .completionSuggestion("name.suggest")
//                    .prefix(keyword)
//                    .size(10)
//                    .skipDuplicates(true);
//
//            SuggestBuilder suggestBuilder = new SuggestBuilder()
//                    .addSuggestion("product-suggest", suggestionBuilder);
//
//            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                    .withSuggestBuilder(suggestBuilder)
//                    .build();
//
//            SearchHits<ESProduct> searchHits = elasticsearchRestTemplate.search(
//                    searchQuery, ESProduct.class, IndexCoordinates.of("product"));
//
//            // 处理suggest结果
//            Suggest suggest = searchHits.getSuggest();
//            if (suggest != null) {
//                CompletionSuggestion completionSuggestion = suggest.getSuggestion("product-suggest");
//                if (completionSuggestion != null) {
//                    return completionSuggestion.getEntries().stream()
//                            .flatMap(entry -> entry.getOptions().stream())
//                            .map(option -> option.getText().string())
//                            .distinct()
//                            .limit(10)
//                            .collect(Collectors.toList());
//                }
//            }
//
//            // 如果Completion Suggester没有结果，降级到普通查询
//            return getFallbackSuggestionsFromES(keyword);
//
//        } catch (Exception e) {
//            log.error("从ES获取搜索建议失败", e);
//            return getFallbackSuggestionsFromES(keyword);
//        }
//    }
//
//    /**
//     * 降级方案：使用普通查询获取搜索建议
//     */
//    private List<String> getFallbackSuggestionsFromES(String keyword) {
//        try {
//            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                    .withQuery(QueryBuilders.wildcardQuery("name.keyword", "*" + keyword + "*"))
//                    .withPageable(PageRequest.of(0, 15))
//                    .build();
//
//            SearchHits<ESProduct> searchHits = elasticsearchRestTemplate.search(
//                    searchQuery, ESProduct.class, IndexCoordinates.of("product"));
//
//            return searchHits.getSearchHits().stream()
//                    .map(hit -> hit.getContent().getName())
//                    .distinct()
//                    .limit(10)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("降级查询也失败了", e);
//            return List.of();
//        }
//    }
//
//    /**
//     * 最终降级方案
//     */
//    private List<String> getFallbackSuggestions(String keyword) {
//        // 返回一些基于关键字的静态建议
//        if (keyword.contains("手机")) {
//            return Arrays.asList("智能手机", "苹果手机", "华为手机", "小米手机");
//        } else if (keyword.contains("电脑")) {
//            return Arrays.asList("笔记本电脑", "台式电脑", "游戏电脑", "苹果电脑");
//        }
//        return Arrays.asList("热门商品", "新品上市", "特价优惠");
//    }
//
//    @Override
//    public List<String> getHotKeywords() {
//        try {
//            // 优先从Redis获取热门关键词
//            Set<ZSetOperations.TypedTuple<Object>> hotKeywords = redisTemplate.opsForZSet()
//                    .reverseRangeWithScores(HOT_KEYWORDS_KEY, 0, HOT_KEYWORDS_SIZE - 1);
//
//            if (hotKeywords != null && !hotKeywords.isEmpty()) {
//                return hotKeywords.stream()
//                        .map(tuple -> tuple.getValue().toString())
//                        .collect(Collectors.toList());
//            }
//
//            // Redis中没有，从ES聚合查询获取
//            return getHotKeywordsFromES();
//
//        } catch (Exception e) {
//            log.error("获取热门搜索词失败", e);
//            return getDefaultHotKeywords();
//        }
//    }
//
//    /**
//     * 从ES聚合查询获取热门关键词
//     */
//    private List<String> getHotKeywordsFromES() {
//        try {
//            // 假设你有一个字段记录搜索次数，或者使用商品名称进行聚合
//            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                    .withQuery(QueryBuilders.matchAllQuery())
//                    .addAggregation(AggregationBuilders.terms("hot_keywords")
//                            .field("name.keyword")
//                            .size(HOT_KEYWORDS_SIZE))
//                    .build();
//
//            SearchHits<ESProduct> searchHits = elasticsearchRestTemplate.search(
//                    searchQuery, ESProduct.class, IndexCoordinates.of("product"));
//
//            Terms terms = searchHits.getAggregations().get("hot_keywords");
//            return terms.getBuckets().stream()
//                    .map(Terms.Bucket::getKeyAsString)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("从ES获取热门关键词失败", e);
//            return getDefaultHotKeywords();
//        }
//    }
//
//    /**
//     * 默认热门关键词
//     */
//    private List<String> getDefaultHotKeywords() {
//        return Arrays.asList("手机", "笔记本电脑", "耳机", "显示器", "键盘",
//                "鼠标", "平板电脑", "相机", "打印机", "路由器");
//    }
//
//    @Override
//    public List<String> getRecentSearches(Long userId) {
//        if (userId == null) {
//            return List.of();
//        }
//
//        try {
//            String key = USER_SEARCH_HISTORY_PREFIX + userId;
//            // 使用List获取最近的搜索记录
//            List<Object> recentSearches = redisTemplate.opsForList().range(key, 0, MAX_HISTORY_SIZE - 1);
//
//            if (recentSearches != null) {
//                return recentSearches.stream()
//                        .map(Object::toString)
//                        .collect(Collectors.toList());
//            }
//        } catch (Exception e) {
//            log.error("获取用户搜索记录失败, userId: {}", userId, e);
//        }
//
//        return List.of();
//    }
//
//    /**
//     * 记录用户搜索行为（在Controller中调用）
//     */
//    public void recordUserSearch(Long userId, String keyword) {
//        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
//            return;
//        }
//
//        String normalizedKeyword = keyword.trim();
//
//        try {
//            // 记录用户搜索历史
//            recordUserSearchHistory(userId, normalizedKeyword);
//
//            // 更新热门搜索词热度
//            updateHotKeywords(normalizedKeyword);
//
//        } catch (Exception e) {
//            log.error("记录用户搜索行为失败, userId: {}, keyword: {}", userId, normalizedKeyword, e);
//        }
//    }
//
//    /**
//     * 记录用户搜索历史
//     */
//    private void recordUserSearchHistory(Long userId, String keyword) {
//        String key = USER_SEARCH_HISTORY_PREFIX + userId;
//
//        // 先移除已有的相同关键词（避免重复）
//        redisTemplate.opsForList().remove(key, 0, keyword);
//
//        // 添加到列表开头
//        redisTemplate.opsForList().leftPush(key, keyword);
//
//        // 修剪列表，保持最大长度
//        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY_SIZE - 1);
//
//        // 设置过期时间（30天）
//        redisTemplate.expire(key, 30, TimeUnit.DAYS);
//    }
//
//    /**
//     * 更新热门搜索词
//     */
//    private void updateHotKeywords(String keyword) {
//        // 使用ZSET，分数为搜索次数
//        redisTemplate.opsForZSet().incrementScore(HOT_KEYWORDS_KEY, keyword, 1);
//
//        // 设置过期时间（永久存储热门词）
//        redisTemplate.expire(HOT_KEYWORDS_KEY, 365, TimeUnit.DAYS);
//    }
//
//    /**
//     * 清空用户搜索历史
//     */
//    public void clearUserSearchHistory(Long userId) {
//        if (userId == null) {
//            return;
//        }
//
//        try {
//            String key = USER_SEARCH_HISTORY_PREFIX + userId;
//            redisTemplate.delete(key);
//        } catch (Exception e) {
//            log.error("清空用户搜索历史失败, userId: {}", userId, e);
//        }
//    }
//
//    /**
//     * 获取搜索建议V2（带权重）
//     */
//    public List<Map<String, Object>> getWeightedSuggestions(String keyword) {
//        List<String> suggestions = getSuggestions(keyword);
//
//        return suggestions.stream()
//                .map(suggestion -> {
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("text", suggestion);
//                    result.put("score", calculateSuggestionScore(keyword, suggestion));
//                    return result;
//                })
//                .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 计算建议词得分
//     */
//    private double calculateSuggestionScore(String keyword, String suggestion) {
//        // 简单的得分计算：匹配长度、前缀匹配等
//        double score = 0;
//        if (suggestion.startsWith(keyword)) {
//            score += 2.0;
//        }
//        if (suggestion.contains(keyword)) {
//            score += 1.0;
//        }
//        // 可以根据业务需求添加更多评分规则
//        return score;
//    }
}