package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.Community;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {

    int insert(Community community);

    Community selectById(Long id);

    List<Community> selectByCategory(@Param("categoryKey") String categoryKey,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    int countByCategory(@Param("categoryKey") String categoryKey);

    int incrementMemberCount(Long id);

    int decrementMemberCount(Long id);

    int incrementTopicCount(Long id);

    // 新增方法：根据 category_key 查询社区列表，用于推荐逻辑
    List<Community> selectByCommunityCategoryKey(@Param("categoryKey") String categoryKey);
}