package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.DonationEvent;

import java.util.List;
import java.util.Map;

/**
 * 事件/活动的服务接口（只保留 donation_event 为主表的操作）
 * 说明：已删除对 Activity 的相关方法，以彻底不依赖 Activity 表。
 */
public interface EventService {

    // 基本的 CRUD 和分页/筛选（DonationEvent 为主表）
    DonationEvent getById(Long id);

    List<DonationEvent> list(Map<String, Object> params);

    Long create(DonationEvent event);

    void update(DonationEvent event);

    void delete(Long id);

    // 管理端分页与计数（DonationEvent 作为主表数据源）
    List<DonationEvent> listAdmin(Map<String, Object> params);

    int countAdmin(Map<String, Object> params);

    // 待审核列表与计数（如果你的 DonationEvent 模型仍包含待审核状态）
    List<DonationEvent> listPending(Map<String, Object> params);
    int countPending(Map<String, Object> params);

    // 审核结果处理
    void applyReviewResult(Long eventId, String action, String comment);

    // 统计相关（按事件维度统计）
    Map<String, Object> countExchangeByEventId(Long eventId);

    // 新增：统计某活动参与人数（基于 donation_item 的 donor_id 去重统计）
    long countParticipantsByEventId(Long eventId);

    // 获取某个事件（若你有需要）
    DonationEvent getActivityById(Long id);

    // 兼容性：若某处仍需要获取正在进行的事件列表，请保留一个明确的方法名
    List<DonationEvent> selectOngoing();
}