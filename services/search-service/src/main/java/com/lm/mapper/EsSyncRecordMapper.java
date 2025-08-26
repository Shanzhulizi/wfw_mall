package com.lm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lm.es.domain.EsSyncRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EsSyncRecordMapper extends BaseMapper<EsSyncRecord> {

    @Select("SELECT DISTINCT spu_id FROM es_sync_record WHERE status = 0 AND retry_count < 3")
    List<Long> selectNeedRetrySpuIds();

//    @Insert("INSERT INTO es_sync_record (spu_id, status, error_msg, retry_count, batch_id, create_time, update_time) " +
//            "VALUES (#{spuId}, #{status}, #{errorMsg}, #{retryCount}, #{batchId}, #{createTime}, #{updateTime})")
//    void insert(EsSyncRecord record);
}
