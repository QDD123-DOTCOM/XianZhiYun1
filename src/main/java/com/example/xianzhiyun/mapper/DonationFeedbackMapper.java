package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.DonationFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DonationFeedbackMapper {
    DonationFeedback selectById(@Param("id") Long id);
    List<DonationFeedback> selectList(@Param("params") Map<String, Object> params);
    int insert(DonationFeedback feedback);
    int deleteById(@Param("id") Long id);
}