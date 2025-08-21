package com.lm.user.mapper;

import com.lm.user.domain.Merchant;
import com.lm.user.domain.MerchantApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantMapper {



    void insertApplication(MerchantApplication application);



    MerchantApplication selectById(Long id);

    void update(MerchantApplication app);

    List<MerchantApplication> selectByStatus(@Param("status") int status);

    List<MerchantApplication> selectByStatusList(@Param("statusList") List<Integer> statusList);

    List<MerchantApplication> selectAll();

    /**
     * 插入商家信息
     * @param app 商家申请信息
     */
    void insert(Merchant app);

    void setApplicationMerchantId(Long merchantId);
}
