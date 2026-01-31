package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.DonationItem;
import java.util.List;
import java.util.Map;

public interface DonationService {
    DonationItem getById(Long id);
    List<DonationItem> list(Map<String, Object> params);
    int count(Map<String, Object> params);
    Long create(DonationItem item);
    void update(DonationItem item);
    void delete(Long id);

    // 新增：按 map 更新状态或其它字段（简化版）
    void updateStatus(Map<String, Object> params);
    // 新增：统计用户参与的不同活动数量
    long countDistinctEventsByUserId(Long userId);

    // 新增（可选）：统计某活动下物品数量
    int countByEventId(Long eventId);
}