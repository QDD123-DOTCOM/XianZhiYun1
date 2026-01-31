package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.xianzhiyun.entity.DonationItem;

import java.util.List;
import java.util.Map;

@Mapper
public interface DonationItemMapper {
    DonationItem selectById(@Param("id") Long id);

    List<DonationItem> selectList(@Param("params") Map<String, Object> params);
    int selectCount(@Param("params") Map<String, Object> params);

    int insert(DonationItem item);
    int update(DonationItem item);
    int deleteById(@Param("id") Long id);
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    // 用于按 ids 批量查询
    List<DonationItem> selectByIds(@Param("ids") List<Long> ids);
    Integer countDistinctEventsByUserId(@Param("userId") Long userId);

    // （可选）统计某活动的物品数量
    Integer countByEventId(@Param("eventId") Long eventId);
}