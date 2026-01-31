package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.DonationEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DonationEventMapper {
    DonationEvent selectById(@Param("id") Long id);
    List<DonationEvent> selectList(@Param("params") Map<String, Object> params);
    int insert(DonationEvent event);
    int update(DonationEvent event);
    int deleteById(@Param("id") Long id);

    // 管理端分页与计数
    List<DonationEvent> selectAdminList(@Param("params") Map<String, Object> params);
    int countAdmin(@Param("params") Map<String, Object> params);

    // 待审核列表与计数
    List<DonationEvent> selectPendingList(@Param("params") Map<String, Object> params);
    int countPending(@Param("params") Map<String, Object> params);

    // 统计交换数量（如果实现）
    Map<String, Object> countExchangeByEventId(@Param("eventId") Long eventId);

    // ongoing
    List<DonationEvent> selectOngoing();
}