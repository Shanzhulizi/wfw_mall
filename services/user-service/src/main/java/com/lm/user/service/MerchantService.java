package com.lm.user.service;

import com.lm.user.domain.MerchantApplication;

import java.util.List;

public interface MerchantService {


    void apply(MerchantApplication application);



    void audit(Long id, Integer status, String reason);

    List<MerchantApplication> listByStatus(int i);

    List<MerchantApplication> listReviewed();

    List<MerchantApplication> listAll();
}
