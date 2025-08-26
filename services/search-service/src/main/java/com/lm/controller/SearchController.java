package com.lm.controller;

import com.lm.es.dto.SearchRequestDTO;
import com.lm.es.vo.SearchResultVO;
import com.lm.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.lm.common.R;
@Controller
@Slf4j
@ResponseBody
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

//    @Autowired
//    private SearchService searchService;

//    @Autowired
//    private ESDataImportService productImportService;
//    /**
//     * 商品搜索
//     */
//    @GetMapping("/search")
//    public R searchProducts(SearchRequestDTO request) {
//        try {
//            SearchResultVO result = searchService.searchProducts(request);
//            return R.ok("搜索成功", result);
//        } catch (Exception e) {
//            log.error("搜索商品失败", e);
//            return R.error("搜索失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取搜索建议
//     */
//    @GetMapping("/suggestions")
//    public R getSuggestions(@RequestParam String keyword) {
//        try {
//            List<String> suggestions = searchService.getSuggestions(keyword);
//            return R.ok("",suggestions);
//        } catch (Exception e) {
//            log.error("获取搜索建议失败", e);
//            return R.error("获取搜索建议失败");
//        }
//    }
//
//    /**
//     * 获取热门搜索词
//     */
//    @GetMapping("/hot-keywords")
//    public R getHotKeywords() {
//        try {
//            List<String> hotKeywords = searchService.getHotKeywords();
//            return R.ok("",hotKeywords);
//        } catch (Exception e) {
//            log.error("获取热门搜索词失败", e);
//            return R.error("获取热门搜索词失败");
//        }
//    }
//
//    /**
//     * 导入单个商品到ES
//     */
//    @PostMapping("/import/{productId}")
//    public R importProduct(@PathVariable Long productId) {
//        try {
//            boolean success = productImportService.importProduct(productId);
//            if (success) {
//                return R.ok("导入成功");
//            } else {
//                return R.error("导入失败");
//            }
//        } catch (Exception e) {
//            log.error("导入商品失败", e);
//            return R.error("导入失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 批量导入商品到ES
//     */
//    @PostMapping("/import/batch")
//    public R importProducts(@RequestBody List<Long> productIds) {
//        try {
//            int successCount = productImportService.importProducts(productIds);
//            return R.ok("成功导入 " + successCount + " 个商品", successCount);
//        } catch (Exception e) {
//            log.error("批量导入商品失败", e);
//            return R.error("批量导入失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 全量同步商品到ES
//     */
//    @PostMapping("/import/full-sync")
//    public R fullSyncProducts() {
//        try {
//            int successCount = productImportService.fullSyncProducts();
//            return R.ok("全量同步完成，成功导入 " + successCount + " 个商品", successCount);
//        } catch (Exception e) {
//            log.error("全量同步失败", e);
//            return R.error("全量同步失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 删除ES中的商品
//     */
//    @DeleteMapping("/import/{productId}")
//    public R deleteProduct(@PathVariable Long productId) {
//        try {
//            boolean success = productImportService.deleteProduct(productId);
//            if (success) {
//                return R.ok("删除成功");
//            } else {
//                return R.error("删除失败");
//            }
//        } catch (Exception e) {
//            log.error("删除ES商品失败", e);
//            return R.error("删除失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 批量删除ES中的商品
//     */
//    @DeleteMapping("/import/batch")
//    public R deleteProducts(@RequestBody List<Long> productIds) {
//        try {
//            int successCount = productImportService.deleteProducts(productIds);
//            return R.ok("成功删除 " + successCount + " 个商品", successCount);
//        } catch (Exception e) {
//            log.error("批量删除ES商品失败", e);
//            return R.error("批量删除失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取ES索引中的商品数量
//     */
//    @GetMapping("/import/count")
//    public R getIndexCount() {
//        try {
//            long count = productImportService.getIndexCount();
//            return R.ok("获取成功", count);
//        } catch (Exception e) {
//            log.error("获取索引数量失败", e);
//            return R.error("获取数量失败: " + e.getMessage());
//        }
//    }
}
