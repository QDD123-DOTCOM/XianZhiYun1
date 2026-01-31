package com.example.xianzhiyun.service;

import java.util.List;

public interface CommunityMemberService {
    /**
     * 当前用户是否已加入指定社区
     */
    boolean isMember(Long communityId, Long userId);

    /**
     * 加入社区，返回 true 表示成功或已加入
     */
    boolean join(Long communityId, Long userId);

    /**
     * 退出社区，返回 true 表示成功或已经不在成员中
     */
    boolean leave(Long communityId, Long userId);

    /**
     * 获取某社区的成员 id（按加入时间降序，limit 限制数量）
     */
    List<Long> listMemberIds(Long communityId, int limit);
}