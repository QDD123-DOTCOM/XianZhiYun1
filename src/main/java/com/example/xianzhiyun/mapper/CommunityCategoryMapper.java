package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Param;
import com.example.xianzhiyun.entity.CommunityCategory;

import java.util.List;

public interface CommunityCategoryMapper {
    int insert(CommunityCategory category);
    int update(CommunityCategory category);
    int deleteById(@Param("id") Long id);
    CommunityCategory selectById(@Param("id") Long id);
    CommunityCategory selectByKey(@Param("categoryKey") String categoryKey);
    List<CommunityCategory> selectAllActive();
}