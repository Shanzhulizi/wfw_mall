package com.lm.user.service.impl;

import com.lm.common.utils.IdGenerator;
import com.lm.user.domain.Merchant;
import com.lm.user.domain.MerchantApplication;
import com.lm.user.mapper.MerchantMapper;
import com.lm.user.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 使用BCrypt加密密码

    @Override
    public void apply(MerchantApplication application) {
        application.setApplicationStatus(0);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        // 加密密码（推荐用 BCrypt）
        String rawPassword = application.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword); // Spring Security 提供的工具
        application.setPassword(encodedPassword);

        merchantMapper.insertApplication(application);
    }

    /**
     * 这里用的是申请商家的记录的id
     * @param id
     * @param status
     * @param reason
     */
    @Override
    public void audit(Long id, Integer status, String reason) {
        MerchantApplication app = merchantMapper.selectById(id);
        if (app == null) {
            throw new RuntimeException("申请记录不存在");
        }
        app.setApplicationStatus(status);
        app.setReason(reason);

        if (status == 1) {

            IdGenerator idGen = new IdGenerator(1,1);
            Long merchantId = idGen.nextId();
            app.setMerchantId(merchantId);
            app.setUpdatedAt(LocalDateTime.now());
            Merchant merchant = new Merchant();
            merchant.setId(merchantId);
            merchant.setPhone(app.getContactPhone());
            merchant.setPassword(app.getPassword()); // 使用加密后的密码
            merchant.setShopName(app.getMerchantName());
//            merchant.setBusinessLicense(businessLicense.toString()); // 假设营业执照是一个字符串
            merchant.setStatus(1); // 正常营业
            // 商家描述空着，以后让商家自己去完善
            merchant.setCreateTime(LocalDateTime.now()); // 设置创建时间为当前时间

            merchantMapper.insert(merchant); // 假设有一个方法将商家信息插入到商家表中

        }

        merchantMapper.update(app);
    }

    @Override
    public List<MerchantApplication> listByStatus(int i) {
        return merchantMapper.selectByStatus(i);
    }

    @Override
    public List<MerchantApplication> listReviewed() {
        List<Integer> statusList = Arrays.asList(1, 2); // 1: 通过, 2: 拒绝
        return merchantMapper.selectByStatusList(statusList);
    }

    @Override
    public List<MerchantApplication> listAll() {
        return merchantMapper.selectAll();
    }



    @Override
    public Merchant getById(Long id) {
        try {
            return merchantMapper.selectMerchantById(id);
        } catch (Exception e) {
            log.error("获取商家信息失败: {}", id, e);
            return null;
        }
    }

    @Override
    public List<Merchant> listByIds(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return new ArrayList<>();
            }
            return merchantMapper.selectByIds(ids);
        } catch (Exception e) {
            log.error("批量获取商家信息失败", e);
            return new ArrayList<>();
        }
    }

}
