package com.lm.product.controller;

import com.lm.common.R;
import com.lm.es.ESProduct;
import com.lm.product.dto.ProductCartDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import com.lm.product.dto.ProductRecommendDTO;
import com.lm.product.mapper.ProductMapper;
import com.lm.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@ResponseBody
@Controller
@RequestMapping("/product")
public class ProductController {


    @Autowired
    ProductService productService;

    @GetMapping("/preloadStock")
    public R preloadStockToRedis(){
        productService.preloadStockToRedis();
        return R.ok("商品库存预热完成");

    }

    @PostMapping("/getByIds")
    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(@RequestBody List<Long> ids){
        List<ProductPriceValidationDTO> list =   productService.getProductPriceValidationDTOsByIds(ids);

        if (list == null || list.isEmpty()) {
            log.warn("No products found for the provided IDs: {}", ids);
            return List.of(); // Return an empty list if no products are found
        }

        return list;
    }



    @GetMapping("/{skuId}")
    ProductCartDTO getProductById(@PathVariable("skuId") Long skuId){
        ProductCartDTO productCartDTO = productService.getProductById(skuId);
        if (productCartDTO == null) {
            log.warn("Product not found for skuId: {}", skuId);
            return null; // or throw an exception, or return a default value
        }
        return productCartDTO;
    }
    // 获取推荐商品（分页，下拉刷新用）
//    @GetMapping("/recommend")
//    public List<ProductRecommendDTO> getRecommendedProducts(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        return productService.getRecommendedProducts(page, size);
//    }
    @GetMapping("/recommend")
    public R getRecommendedProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProductRecommendDTO> products = productService.getRecommendedProducts(page, size);
        return R.ok("返回推荐",products);
    }




    @Autowired
    private ProductMapper productMapper;
    //外部调用
    @GetMapping("/listAllForSearch")
    public List<ESProduct> listAllForSearch() {
        return productMapper.listAllForSearch();
    }
}
