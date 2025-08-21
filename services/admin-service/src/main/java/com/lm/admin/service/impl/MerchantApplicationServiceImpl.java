package com.lm.admin.service.impl;

import com.lm.admin.feign.UserFeignClient;
import com.lm.admin.service.MerchantApplicationService;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.dto.AuditDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MerchantApplicationServiceImpl implements MerchantApplicationService {

    @Autowired
    UserFeignClient userFeignClient;
    @Override
    public List<MerchantApplication> listByStatus(int i) {
        return  userFeignClient.getApplicationsByStatus(i);
//        return null;
    }

    @Override
    public List<MerchantApplication> listReviewed() {
        return  userFeignClient.getApplicationsReviewed();
//        return null;
    }

    @Override
    public List<MerchantApplication> listAll() {
        return  userFeignClient.getApplicationsAll();
//        return null;
    }

    @Override
    public void audit(Long id, Integer status, String reason) {
        AuditDTO auditDTO = new AuditDTO();
        auditDTO.setId(id);
        auditDTO.setStatus(status);
        auditDTO.setReason(reason);

        userFeignClient.audit(auditDTO);
    }


}
