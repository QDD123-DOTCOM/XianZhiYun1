package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.Community;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {

    List<Community> selectByCategory(@Param("categoryKey") String categoryKey, @Param("offset") int offset, @Param("limit") int limit);

    int countByCategory(@Param("categoryKey") String categoryKey);

    Community selectById(@Param("id") Long id);

    void incrementMemberCount(@Param("id") Long id);

    void decrementMemberCount(@Param("id") Long id);

    void incrementTopicCount(@Param("id") Long id);

    /**
     * 查询我加入的社区列表
     */
    List<Community> selectMyJoined(@Param("userId") Long userId);

    List<Community> selectByCommunityCategoryKey(@Param("categoryKey") String categoryKey);
}