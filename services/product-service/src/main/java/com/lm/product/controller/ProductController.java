package com.lm.product.controller;

import com.lm.common.R;
import com.lm.product.dto.*;
import com.lm.product.service.ProductService;
import com.lm.product.vo.ProductDetailVO;
import com.lm.product.vo.ProductSkuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@ResponseBody
@Controller
@RequestMapping("/product")
public class ProductController {


    @Autowired
    ProductService productService;

    @GetMapping("/preloadStock")
    public R preloadStockToRedis() {
        productService.preloadStockToRedis();
        return R.ok("商品库存预热完成");

    }

    @PostMapping("/getByIds")
    List<ProductPriceValidationDTO> getProductPriceValidationDTOsByIds(@RequestBody List<Long> ids) {
        List<ProductPriceValidationDTO> list = productService.getProductPriceValidationDTOsByIds(ids);

        if (list == null || list.isEmpty()) {
            log.warn("No products found for the provided IDs: {}", ids);
            return new ArrayList<>(); // Return an empty list if no products are found
        }

        return list;
    }

    @GetMapping("/skuInfo")
    public ProductSkuVO getSkuInfo(@RequestParam("skuId") Long skuId) {


        ProductSkuVO skuInfo = productService.getSkuInfo(skuId);
        if (skuInfo == null) {
            log.warn("SKU not found for skuId: {}", skuId);
            return null; // or throw an exception, or return a default value
        }
        return skuInfo;
    }
    @GetMapping("/{skuId}")
    ProductCartDTO getCartProductById(@PathVariable("skuId") Long skuId) {
        ProductCartDTO productCartDTO = productService.getProductById(skuId);
        if (productCartDTO == null) {
            log.warn("Product not found for skuId: {}", skuId);
            return null; // or throw an exception, or return a default value
        }
        return productCartDTO;
    }

    @GetMapping("/recommend")
    public R getRecommendedProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ProductRecommendDTO> products = productService.getRecommendedProducts(page, size);
        return R.ok("返回推荐", products);
    }

    /**
     * 根据ID获取商品
     */
    @GetMapping("/detail/{id}")
    public R getProductById(@PathVariable Long id) {
        try {
            ProductDetailVO product = productService.getProductDetailById(id);
            if (product == null) {
                return R.error("商品不存在");
            }
            return R.ok("",product);
        } catch (Exception e) {
            log.error("获取商品信息失败: {}", e.getMessage(), e);
            return R.error("获取商品信息失败");
        }
    }


    // ProductSpuController.java
    @GetMapping("/spu/list")
    public R listSpus(@RequestParam(required = false) Long lastUpdateTime,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "100") int size) {
        try {
            List<ProductSpuDTO> spuList;

            if (lastUpdateTime != null) {
                Date sinceTime = new Date(lastUpdateTime);
                spuList = productService.getSpusUpdatedAfter(sinceTime, page, size);
            } else {
                spuList = productService.getAllSpus(page, size);
            }

            log.info("查询成功: 条件={}, 页码={}, 大小={}, 结果数={}",
                    lastUpdateTime != null ? "增量" : "全量", page, size, spuList.size());

            return R.ok("查询成功", spuList);
        } catch (Exception e) {
            log.error("查询SPU列表失败", e);
            return R.error("查询失败: " + e.getMessage());
        }
    }


    @GetMapping("/category/{id}")
    R getCategoryById(@PathVariable Long id) {
        ProductCategoryDTO category = productService.getCategoryById(id);
        if (category == null) {
            return R.error("分类不存在");
        }
        return R.ok("", category);
    }

    @GetMapping("/brand/{id}")
    R getBrandById(@PathVariable Long id) {
        ProductBrandDTO brand = productService.getBrandById(id);
        if (brand == null) {
            return R.error("品牌不存在");
        }
        return R.ok("", brand);
    }

    @GetMapping("/sku/bySpu/{spuId}")
    R getSkusBySpuId(@PathVariable Long spuId) {

        List<ProductSkuDTO> skus = productService.getSkusBySpuId(spuId);

        if (skus == null || skus.isEmpty()) {
            return R.error("该SPU下无SKU");
        }
        return R.ok("", skus);

    }

    @GetMapping("/spu/{spuId}")
    R getSpuById(Long spuId){
        ProductSpuDTO spu = productService.getSpuById(spuId);
        if (spu == null) {
            return R.error("SPU不存在");
        }
        return R.ok("", spu);
    }
}