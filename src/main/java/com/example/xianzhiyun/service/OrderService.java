package com.example.xianzhiyun.service;

import com.example.xianzhiyun.dto.CreateOrderDTO;
import com.example.xianzhiyun.entity.Orders;

import java.util.Map;

public interface OrderService {
    Long createOrder(Long buyerId, CreateOrderDTO dto);
    Orders getById(Long id);
    Map<String, Object> listOrders(Long buyerId, String status, Integer shipped, int page, int pageSize);
    Map<String, Object> statsByBuyer(Long buyerId);
    boolean confirmReceipt(Long orderId, Long buyerId);
    boolean confirmOrderItem(Long orderId, Long itemId, Long buyerId);

    Map<String, Object> confirmOrderItemWithCredit(Long orderId, Long itemId, Long buyerId);
    boolean shipOrderItem(Long orderId, Long itemId, Long sellerId,
                          String expressCompany, String trackingNumber, String shipProof);

    // 新增：卖家侧按 sellerId 列表（只返回包含该 seller 的 item）
    Map<String, Object> listOrdersForSeller(Long sellerId, String status, Integer shipped, int page, int pageSize);
    Map<String, Object> listRelatedOrders(Long userId, String status, int page, int pageSize);
}