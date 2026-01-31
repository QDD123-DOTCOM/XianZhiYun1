package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.CommunityMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommunityMemberMapper {

    // 检查是否已加入，返回匹配记录数
    int exists(@Param("communityId") Long communityId, @Param("userId") Long userId);

    // 获取前N个成员ID（用于头像展示）
    List<Long> selectUserIdsByCommunity(@Param("communityId") Long communityId, @Param("limit") int limit);

    // 加入社区
    int insert(CommunityMember member);

    // 退出社区
    int delete(@Param("communityId") Long communityId, @Param("userId") Long userId);

    // 统计社区总人数
    int countByCommunityId(@Param("communityId") Long communityId);

    // 根据社区ID和用户ID查询成员记录
    CommunityMember selectByCommunityAndUser(@Param("communityId") Long communityId, @Param("userId") Long userId);

    // 根据社区ID和用户ID删除成员记录（退出社区）
    int deleteByCommunityAndUser(@Param("communityId") Long communityId, @Param("userId") Long userId);
}