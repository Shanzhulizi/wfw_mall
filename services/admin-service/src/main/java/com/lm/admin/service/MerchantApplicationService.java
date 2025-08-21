package com.lm.admin.service;

import com.lm.user.domain.MerchantApplication;

import java.util.List;

public interface MerchantApplicationService {
    List<MerchantApplication> listByStatus(int i);

    List<MerchantApplication> listReviewed();

    List<MerchantApplication> listAll();

    void audit(Long id, Integer status, String reason);
}
