package com.lm.product.mapper;

import com.lm.product.dto.ProductPreloadDTO;
import com.lm.product.dto.ProductPriceValidationDTO;
import org.apache.ibatis.annotations.Mapper;

import javax.management.MXBean;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductPreloadDTO> selectAllAvailable();


    /**
     * 根据商品ID列表查询商品价格校验信息
     * @param ids
     * @return
     */
    List<ProductPriceValidationDTO> selectPriceValidationByIds(List<Long> ids);
}
