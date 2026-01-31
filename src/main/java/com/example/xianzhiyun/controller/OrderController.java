package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.dto.CreateOrderDTO;
import com.example.xianzhiyun.entity.OrderItem;
import com.example.xianzhiyun.entity.Orders;
import com.example.xianzhiyun.service.OrderService;
import com.example.xianzhiyun.mapper.OrderItemMapper;
import com.example.xianzhiyun.mapper.OrderAddressMapper;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderController - 下单与查询相关接口
 * 修复：全面替换 getHeader 为 @RequestAttribute("userId")
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired(required = false)
    private OrderAddressMapper orderAddressMapper;

    @PostMapping("/order")
    public JsonResult createOrder(@RequestBody CreateOrderDTO dto,
                                  @RequestAttribute("userId") Long buyerId) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            return JsonResult.error(400, "商品列表不能为空");
        }
        Long orderId = orderService.createOrder(buyerId, dto);
        return JsonResult.success(orderId);
    }

    @GetMapping("/user/orders")
    public JsonResult listOrders(@RequestAttribute("userId") Long uid,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "shipped", required = false) String shippedStr,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Integer shipped = parseShippedParam(shippedStr);
        Map<String, Object> data = orderService.listOrders(uid, status, shipped, page, pageSize);
        return JsonResult.success(data);
    }

    private Integer parseShippedParam(String shippedStr) {
        if (shippedStr == null) return null;
        shippedStr = shippedStr.trim();
        if (shippedStr.isEmpty()) return null;
        String low = shippedStr.toLowerCase();
        if ("null".equals(low) || "undefined".equals(low)) return null;
        try {
            return Integer.parseInt(shippedStr);
        } catch (Exception ignored) {
            return null;
        }
    }

    @GetMapping("/user/orders/stats")
    public JsonResult ordersStats(@RequestAttribute("userId") Long uid) {
        Map<String, Object> stats = orderService.statsByBuyer(uid);
        return JsonResult.success(stats);
    }

    @GetMapping("/order/{id:\\d+}")
    public JsonResult getOrder(@PathVariable Long id,
                               @RequestAttribute("userId") Long uid) {
        Orders o = orderService.getById(id);
        if (o == null) return JsonResult.error(404, "订单不存在");
        if (!uid.equals(o.getBuyerId())) return JsonResult.error(403, "无权限查看该订单");

        Map<String, Object> resp = new HashMap<>();
        resp.put("order", o);
        resp.put("items", o.getItems() == null ? List.of() : o.getItems());
        resp.put("address", o.getAddress());
        return JsonResult.success(resp);
    }

    @PostMapping("/order/{orderId:\\d+}/item/{itemId:\\d+}/confirm")
    public JsonResult confirmOrderItem(@PathVariable Long orderId,
                                       @PathVariable Long itemId,
                                       @RequestAttribute("userId") Long uid) {
        Map<String, Object> svcRes = orderService.confirmOrderItemWithCredit(orderId, itemId, uid);
        if (svcRes == null || svcRes.get("ok") == null || !Boolean.TRUE.equals(svcRes.get("ok"))) {
            return JsonResult.error(400, "确认收货失败");
        }
        return JsonResult.success(svcRes);
    }

    // 内部类也需要修复
    @RestController
    @RequestMapping("/api")
    public static class OrderRelatedController {

        @Autowired
        private OrderService orderService;

        @GetMapping("/orders/related")
        public JsonResult listRelatedOrders(@RequestAttribute("userId") Long userId,
                                            @RequestParam(value="status", required=false) String status,
                                            @RequestParam(value="page", defaultValue="1") int page,
                                            @RequestParam(value="pageSize", defaultValue="10") int pageSize) {
            Map<String,Object> data = orderService.listRelatedOrders(userId, status, page, pageSize);
            return JsonResult.success(data);
        }
    }
}