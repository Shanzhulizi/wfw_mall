package com.lm.controller;

import com.lm.es.ESProduct;
import com.lm.feign.ProductFeignClient;
import com.lm.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@ResponseBody
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {


    @Autowired
    private SearchService searchService;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 这个留着，我觉得应该在商家上传商品的时候添加商品到ES和MySQL，所以它是商家端的接口的一部分
     *
     * @param param
     * @return
     */
//    // 上传商品数据到 ES
//    @PostMapping("/upload")
//    public R uploadProduct(@RequestBody ESProductDTO product) {
//        try {
//            productSearchService.uploadProduct(product);
//            return AjaxResult.success("商品已上传到ES");
//        } catch (Exception e) {
//            return AjaxResult.error("上传失败：" + e.getMessage());
//
//        }
//    }
    @PostMapping("/importAll")
    public void importAllFromDB() throws IOException {
        List<ESProduct> products = productFeignClient.listAllForSearch();
        searchService.bulkInsert(products);
    }

    // 搜索商品
    @GetMapping("/search")
    public List<ESProduct> search(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String brand,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) Double minPrice,
                                  @RequestParam(required = false) Double maxPrice,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) throws IOException {


        return searchService.search(keyword, brand, category, minPrice, maxPrice, page, size);
    }

}
