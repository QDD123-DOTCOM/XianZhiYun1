package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.service.OrderService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerOrderController {

    @Autowired
    private OrderService orderService;

    public static class ShipRequest {
        private String expressCompany;
        private String trackingNumber;
        private String shipProof;
        public String getExpressCompany() { return expressCompany; }
        public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getShipProof() { return shipProof; }
        public void setShipProof(String shipProof) { this.shipProof = shipProof; }
    }

    /**
     * 修复 404：新增统计接口
     * 前端请求：/api/seller/orders/stats?status=PAID
     */
    @GetMapping("/orders/stats")
    public JsonResult<Map<String, Object>> getOrderStats(
            @RequestAttribute("userId") Long sellerId,
            @RequestParam(value = "status", required = false) String status) {

        // 复用 listOrdersForSeller 方法，pageSize=1 仅为了获取 total 字段
        // 这样不需要修改 Service 接口也能实现功能
        Map<String, Object> result = orderService.listOrdersForSeller(sellerId, status, null, 1, 1);

        long count = 0;
        if (result != null && result.containsKey("total")) {
            try {
                count = Long.parseLong(String.valueOf(result.get("total")));
            } catch (Exception e) {
                count = 0;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return JsonResult.success(data);
    }

    /**
     * 修复 401：使用 @RequestAttribute("userId") 替代 Header 获取
     */
    @GetMapping("/orders")
    public JsonResult listSellerOrders(@RequestAttribute("userId") Long sellerId,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "shipped", required = false) String shippedStr,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Integer shipped = parseShippedParam(shippedStr);
        Map<String, Object> data = orderService.listOrdersForSeller(sellerId, status, shipped, page, pageSize);

        return JsonResult.success(data);
    }

    /**
     * 修复 401：使用 @RequestAttribute("userId") 替代 Header 获取
     */
    @PostMapping("/order/{orderId:\\d+}/item/{itemId:\\d+}/ship")
    public JsonResult<Map<String,Object>> shipOrderItem(@PathVariable Long orderId,
                                                        @PathVariable Long itemId,
                                                        @RequestBody ShipRequest body,
                                                        @RequestAttribute("userId") Long sellerId) {

        String expressCompany = (body != null) ? body.getExpressCompany() : null;
        String trackingNumber = (body != null) ? body.getTrackingNumber() : null;
        String shipProof = (body != null) ? body.getShipProof() : null;
        try {
            boolean ok = orderService.shipOrderItem(orderId, itemId, sellerId, expressCompany, trackingNumber, shipProof);
            if (ok) {
                Map<String,Object> data = new HashMap<>();
                data.put("ok", true);
                data.put("needRefreshOrders", true);
                return JsonResult.success(data);
            } else {
                return JsonResult.error(400, "发货失败或已发货");
            }
        } catch (Exception e) {
            return JsonResult.error(500, "内部错误");
        }
    }

    private Integer parseShippedParam(String shippedStr) {
        if (shippedStr == null) return null;
        shippedStr = shippedStr.trim();
        if (shippedStr.isEmpty()) return null;
        if ("null".equalsIgnoreCase(shippedStr) || "undefined".equalsIgnoreCase(shippedStr)) return null;
        try {
            return Integer.valueOf(shippedStr);
        } catch (Exception e) {
            return null;
        }
    }
}