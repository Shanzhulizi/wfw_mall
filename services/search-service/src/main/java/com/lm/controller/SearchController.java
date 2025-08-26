package com.lm.controller;

import com.lm.common.R;
import com.lm.es.dto.ProductSearchRequest;
import com.lm.es.dto.ProductSearchResult;
import com.lm.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@ResponseBody
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private SearchService productSearchService;

    /**
     * 商品搜索接口
     */
    @PostMapping("/products")
    public R searchProducts(@RequestBody ProductSearchRequest request) {
        try {
            ProductSearchResult result = productSearchService.searchProducts(request);
            return R.ok("搜索成功", result);
        } catch (Exception e) {
            log.error("搜索商品异常", e);
            return R.error("搜索失败: " + e.getMessage());
        }
    }
//
//    /**
//     * 简单搜索接口（GET方式）
//     */
//    @GetMapping("/products/simple")
//    public R simpleSearch(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "20") Integer size) {
//
//        log.info("简单搜索: keyword={}, page={}, size={}", keyword, page, size);
//        ProductSearchRequest request = new ProductSearchRequest();
//        request.setKeyword(keyword);
//        request.setPage(page);
//        request.setSize(size);
//
//        return searchProducts(request);
//    }

    /**
     * 获取搜索建议（自动补全）
     */
    @GetMapping("/suggestions")
    public R getSuggestions(@RequestParam String prefix) {
        try {
            // 这里可以实现搜索建议功能
            List<String> suggestions = Collections.emptyList();
            return R.ok("获取成功", suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议异常", e);
            return R.error("获取搜索建议失败");
        }
    }
}
