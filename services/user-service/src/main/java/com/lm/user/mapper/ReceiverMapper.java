package com.lm.user.mapper;

import com.lm.order.dto.ReceiverInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReceiverMapper {


    int countByUserIdAndReceiverInfoId(Long userId, Long receiverInfoId);

    ReceiverInfoDTO getReceiverInfoById(Long receiverInfoId);
}
