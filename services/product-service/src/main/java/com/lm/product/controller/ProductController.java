package com.lm.product.controller;

import com.lm.common.R;
import com.lm.product.domain.ProductSpu;
import com.lm.product.dto.*;
import com.lm.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            return List.of(); // Return an empty list if no products are found
        }

        return list;
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


//    /**
//     * 根据ID获取商品
//     */
//    @GetMapping("/{id}")
//    public R getProductById(@PathVariable Long id) {
//        try {
//            ProductDetailVO product = productService.getProductDetailById(id);
//            if (product == null) {
//                return R.error("商品不存在");
//            }
//            return R.ok("",product);
//        } catch (Exception e) {
//            log.error("获取商品信息失败: {}", e.getMessage(), e);
//            return R.error("获取商品信息失败");
//        }
//    }
//
//    /**
//     * 批量获取商品
//     */
//    @PostMapping("/batch")
//    public R getProductsByIds(@RequestBody List<Long> ids) {
//        try {
//            if (ids == null || ids.isEmpty()) {
//                return R.ok("", Collections.emptyList());
//            }
//
//            List<ProductDetailVO> products = productService.getProductDetailsByIds(ids);
//            return R.ok("",products);
//        } catch (Exception e) {
//            log.error("批量获取商品失败: {}", e.getMessage(), e);
//            return R.error("批量获取商品失败");
//        }
//    }
//
//    /**
//     * 获取所有上架商品 ，废弃
//     */
//    @GetMapping("/list/on-shelf")
//    public R getOnShelfProducts() {
//        try {
//            List<ProductDetailVO> products = productService.getOnShelfProducts();
//            return R.ok("",products);
//        } catch (Exception e) {
//            log.error("获取上架商品失败: {}", e.getMessage(), e);
//            return R.error("获取上架商品失败");
//        }
//    }
//    // 新增分页查询接口
//    @GetMapping("/list/on-shelf-page")
//    public PageResult<ESProduct> getOnShelfProductsByPage(
//            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
//            @RequestParam(value = "pageSize", defaultValue = "500") Integer pageSize) {
//
//        // 使用原生MyBatis分页
//        PageResult<ESProduct> result = productService.getOnShelfProductsByPage(pageNum, pageSize);
//        return result;
//    }
//    /**
//     * 获取指定时间后更新的商品
//     */
//    @GetMapping("/list/updated-after")
//    public R getProductsUpdatedAfter(@RequestParam String afterTime) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = sdf.parse(afterTime);
//
//            List<ProductDetailVO> products = productService.getProductsUpdatedAfter(date);
//            return R.ok("",products);
//        } catch (Exception e) {
//            log.error("获取增量商品失败: {}", e.getMessage(), e);
//            return R.error("获取增量商品失败");
//        }
//    }


//    @GetMapping("/spu/list")
//    R listSpus(@RequestParam(required = false) Long lastUpdateTime) {
//        List<ProductSpuDTO> spus =  productService.listSpus(lastUpdateTime);
//
//
//
//        if (spus == null || spus.isEmpty()) {
//            return R.error("无商品数据");
//        }
//        return R.ok("", spus);
//    }

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


}