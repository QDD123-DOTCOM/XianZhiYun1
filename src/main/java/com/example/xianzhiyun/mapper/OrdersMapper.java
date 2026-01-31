package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    void insertOrder(Orders order);
    Orders selectById(Long id);

    List<Orders> listByBuyer(Map<String, Object> params);
    int countByBuyer(Map<String, Object> params);
    Map<String, Object> statsByBuyer(Long buyerId);

    void updateStatus(Orders order);
    // 新增：列出包含指定 seller 的订单（用于卖家端）
    List<Orders> listBySeller(Map<String, Object> params);

    // 新增：统计包含指定 seller 的订单数（用于分页）
    int countBySeller(Map<String, Object> params);
    List<com.example.xianzhiyun.entity.Orders> listRelatedOrders(Map<String, Object> params);
    int countRelatedOrders(Map<String, Object> params);
}