package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.xianzhiyun.entity.DonationReview;

import java.util.List;
import java.util.Map;

@Mapper
public interface DonationReviewMapper {
    DonationReview selectById(@Param("id") Long id);
    List<DonationReview> selectByItemId(@Param("itemId") Long itemId);
    List<DonationReview> selectList(@Param("params") Map<String, Object> params);
    int insert(DonationReview review);
    int deleteById(@Param("id") Long id);
}