package com.lm.admin.feign.fallback;


import com.lm.admin.feign.UserFeignClient;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;

import java.util.List;

public class UserFeignClientFallback implements UserFeignClient {


    @Override
    public List<MerchantApplication> getApplicationsByStatus(int i) {
        return null;
    }

    @Override
    public List<MerchantApplication> getApplicationsReviewed() {
        return null;
    }

    @Override
    public List<MerchantApplication> getApplicationsAll() {
        return null;
    }

    @Override
    public void audit(AuditDTO auditDTO) {

    }


}
