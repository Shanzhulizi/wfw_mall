package com.lm.product.controller;

import com.lm.common.R;
import com.lm.product.dto.ProductPriceValidationDTO;
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
}
