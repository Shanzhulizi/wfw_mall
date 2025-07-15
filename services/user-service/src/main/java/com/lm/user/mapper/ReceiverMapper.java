package com.lm.user.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReceiverMapper {


    int countByUserIdAndReceiverInfoId(Long userId, Long receiverInfoId);
}
