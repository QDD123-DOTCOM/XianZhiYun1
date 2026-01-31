package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.DonationFeedback;

import java.util.List;

public interface DonationFeedbackService {
    /**
     * 按活动（或物品）ID 列表（eventId）查询反馈，limit 可为 null 表示不限制
     */
    List<DonationFeedback> listByEventId(Long eventId, Integer limit);

    /**
     * 插入一条反馈（若需要）
     */
    Long create(DonationFeedback feedback);

    /**
     * 删除反馈
     */
    void delete(Long id);
}