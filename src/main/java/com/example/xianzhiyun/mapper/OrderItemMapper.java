package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItem item);

    List<OrderItem> selectByOrderId(Long orderId);

    // 新：接收 map 参数（orderId, shipped）
    List<OrderItem> selectByOrderIdAndShipped(Map<String, Object> params);

    OrderItem selectById(Long id);

    // 更新方法接收 map 参数（itemId）
    int markItemReceived(Map<String, Object> params);

    // 更新方法接收 map 参数（itemId, expressCompany, trackingNumber, shipProof）
    int markItemShipped(Map<String, Object> params);
    // 统计某订单下 received = 指定值 的数量
    int countByOrderIdAndReceived(Map<String, Object> params);

    // 将某订单下所有未收到的 item 标记为已收货
    int markItemsReceivedByOrderId(Map<String, Object> params);
    // 查询某订单中与指定用户相关的 items（卖家）
    List<OrderItem> selectByOrderIdAndSeller(Map<String, Object> params);

}