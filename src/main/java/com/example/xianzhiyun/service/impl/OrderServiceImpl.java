package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.CreateOrderDTO;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.entity.OrderAddress;
import com.example.xianzhiyun.entity.OrderItem;
import com.example.xianzhiyun.entity.Orders;
import com.example.xianzhiyun.mapper.CartItemMapper;
import com.example.xianzhiyun.mapper.GoodsMapper;
import com.example.xianzhiyun.mapper.OrderAddressMapper;
import com.example.xianzhiyun.mapper.OrderItemMapper;
import com.example.xianzhiyun.mapper.OrdersMapper;
import com.example.xianzhiyun.mapper.UserInfoMapper;
import com.example.xianzhiyun.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * OrderServiceImpl - 主要实现（含 listOrders/listOrdersForSeller 支持 shipped 参数）
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderAddressMapper orderAddressMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Long createOrder(Long buyerId, CreateOrderDTO dto) {
        OrderAddress addr = new OrderAddress();
        addr.setBuyerId(buyerId);
        addr.setName(dto.getAddress().getName());
        addr.setMobile(dto.getAddress().getMobile());
        addr.setSchool(dto.getAddress().getSchool());
        addr.setRegion(String.join("/", dto.getAddress().getRegion()));
        addr.setDetail(dto.getAddress().getDetail());
        orderAddressMapper.insert(addr);
        double total = 0;
        for (CreateOrderDTO.Item it : dto.getItems()) {
            total += it.getPrice() * it.getQuantity();
        }
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setTotalAmount(total);
        order.setStatus("PAID");
        order.setAddressId(addr.getId());
        order.setNote(dto.getNote());
        ordersMapper.insertOrder(order);
        List<Long> goodsIdsToClear = new ArrayList<>();
        for (CreateOrderDTO.Item it : dto.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setGoodsId(it.getGoodsId());
            if (it.getGoodsId() != null) {
                try {
                    GoodsItem gi = goodsMapper.selectById(it.getGoodsId());
                    if (gi != null && gi.getSellerId() != null) {
                        oi.setSellerId(gi.getSellerId());
                    }
                } catch (Exception ex) { /* ignore */ }
            }
            oi.setPrice(it.getPrice());
            oi.setQuantity(it.getQuantity());
            oi.setAmount(it.getPrice() * it.getQuantity());
            orderItemMapper.insert(oi);
            if (it.getGoodsId() != null) goodsIdsToClear.add(it.getGoodsId());
        }
        if (Boolean.TRUE.equals(dto.getFromCart()) && !goodsIdsToClear.isEmpty()) {
            cartItemMapper.deleteByUserAndGoodsIds(buyerId, goodsIdsToClear);
        }
        return order.getId();
    }

    @Override
    public Orders getById(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(id);
            if (items == null) items = Collections.emptyList();
            for (OrderItem it : items) {
                if ((it.getCover() == null || it.getCover().isEmpty()) && it.getCoverUrls() != null && !it.getCoverUrls().isEmpty()) {
                    String[] arr = it.getCoverUrls().split(",");
                    if (arr.length > 0) it.setCover(arr[0].trim());
                }
            }
            order.setItems(items);
            if (order.getAddressId() != null) order.setAddress(orderAddressMapper.selectById(order.getAddressId()));
        }
        return order;
    }

    @Override
    public Map<String, Object> listOrders(Long buyerId, String status, Integer shipped, int page, int pageSize) {
        System.out.println("DEBUG Service listOrders: buyerId=" + buyerId + ", status=" + status + ", shipped=" + shipped + ", page=" + page + ", pageSize=" + pageSize);
        int offset = (page - 1) * pageSize;
        Map<String,Object> params = new HashMap<>();
        params.put("buyerId", buyerId);
        params.put("status", status);
        params.put("offset", offset);
        params.put("limit", pageSize);
        List<Orders> items = ordersMapper.listByBuyer(params);
        int total = ordersMapper.countByBuyer(params);
        List<Orders> outOrders = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (Orders ord : items) {
                List<OrderItem> orderItems;
                if (shipped != null) {
                    Map<String,Object> p2 = new HashMap<>();
                    p2.put("orderId", ord.getId());
                    p2.put("shipped", shipped);
                    orderItems = orderItemMapper.selectByOrderIdAndShipped(p2);
                } else {
                    orderItems = orderItemMapper.selectByOrderId(ord.getId());
                }
                if (orderItems == null) orderItems = Collections.emptyList();
                for (OrderItem it : orderItems) {
                    if ((it.getCover() == null || it.getCover().isEmpty()) && it.getCoverUrls() != null && !it.getCoverUrls().isEmpty()) {
                        String[] arr = it.getCoverUrls().split(",");
                        if (arr.length > 0) it.setCover(arr[0].trim());
                    }
                }
                if (!orderItems.isEmpty()) {
                    ord.setItems(orderItems);
                    if (ord.getAddressId() != null) ord.setAddress(orderAddressMapper.selectById(ord.getAddressId()));
                    ord.setRoleForCurrentUser("BUYER");
                    outOrders.add(ord);
                }
            }
        }
        Map<String,Object> out = new HashMap<>();
        out.put("items", outOrders);
        out.put("total", total);
        out.put("page", page);
        out.put("pageSize", pageSize);
        return out;
    }

    @Override
    @Transactional
    public boolean confirmReceipt(Long orderId, Long buyerId) {
        Orders o = ordersMapper.selectById(orderId);
        if (o == null) return false;
        if (!Objects.equals(o.getBuyerId(), buyerId)) return false;

        // 标记该订单下所有未收到的 item 为已收货
        Map<String,Object> mp = new HashMap<>();
        mp.put("orderId", orderId);
        orderItemMapper.markItemsReceivedByOrderId(mp);

        // 标记订单为已完成
        Orders update = new Orders();
        update.setId(orderId);
        update.setStatus("FINISHED");
        ordersMapper.updateStatus(update);
        return true;
    }

    @Override
    @Transactional
    public boolean confirmOrderItem(Long orderId, Long itemId, Long buyerId) {
        Orders o = ordersMapper.selectById(orderId);
        if (o == null) return false;
        if (!Objects.equals(o.getBuyerId(), buyerId)) return false;
        OrderItem target = orderItemMapper.selectById(itemId);
        if (target == null) return false;
        Integer s = target.getShipped() == null ? 0 : target.getShipped();
        if (s != 1) return false;
        Map<String,Object> mp = new HashMap<>();
        mp.put("itemId", itemId);
        int updated = orderItemMapper.markItemReceived(mp);
        if (updated <= 0) return false;

        // 使用 DB 计数检查是否还有未收到的 item（received = 0）
        Map<String,Object> p2 = new HashMap<>();
        p2.put("orderId", orderId);
        p2.put("received", 0);
        int notReceived = 0;
        try {
            notReceived = orderItemMapper.countByOrderIdAndReceived(p2);
        } catch (Exception ex) {
            // 回退为 Java 层检查（兼容旧实现）
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            boolean allReceived = true;
            for (OrderItem it : items) {
                Integer rec = it.getReceived() == null ? 0 : it.getReceived();
                if (rec == 0) { allReceived = false; break; }
            }
            if (allReceived) {
                Orders u = new Orders();
                u.setId(orderId);
                u.setStatus("FINISHED");
                ordersMapper.updateStatus(u);
            }
            return true;
        }

        if (notReceived == 0) {
            Orders u = new Orders();
            u.setId(orderId);
            u.setStatus("FINISHED");
            ordersMapper.updateStatus(u);
        }
        return true;
    }

    @Override
    public Map<String,Object> confirmOrderItemWithCredit(Long orderId, Long itemId, Long buyerId) {
        Map<String,Object> res = new HashMap<>();
        Orders o = ordersMapper.selectById(orderId);
        if (o == null) { res.put("ok", false); res.put("msg", "订单不存在"); return res; }
        if (!Objects.equals(o.getBuyerId(), buyerId)) { res.put("ok", false); res.put("msg", "无权限"); return res; }
        OrderItem target = orderItemMapper.selectById(itemId);
        if (target == null) { res.put("ok", false); res.put("msg", "订单商品不存在"); return res; }
        Integer s = target.getShipped() == null ? 0 : target.getShipped();
        if (s != 1) { res.put("ok", false); res.put("msg", "该商品尚未发货"); return res; }
        Map<String,Object> mp = new HashMap<>();
        mp.put("itemId", itemId);
        int updated = orderItemMapper.markItemReceived(mp);
        if (updated <= 0) { res.put("ok", false); res.put("msg", "标记收货失败"); return res; }
        Map<String,Object> p2 = new HashMap<>();
        p2.put("orderId", orderId);
        p2.put("received", 0);
        int notReceived = 0;
        try {
            notReceived = orderItemMapper.countByOrderIdAndReceived(p2);
        } catch (Exception ex) {
            notReceived = -1;
        }
        if (notReceived == 0) {
            Orders u = new Orders();
            u.setId(orderId);
            u.setStatus("FINISHED");
            ordersMapper.updateStatus(u);
        } else if (notReceived == -1) {
            // fallback to Java-layer check (for compatibility)
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            boolean allReceived = true;
            for (OrderItem it : items) {
                Integer rec = it.getReceived() == null ? 0 : it.getReceived();
                if (rec == 0) { allReceived = false; break; }
            }
            if (allReceived) {
                Orders u = new Orders();
                u.setId(orderId);
                u.setStatus("FINISHED");
                ordersMapper.updateStatus(u);
            }
        }

        res.put("ok", true);
        res.put("order", getById(orderId));
        return res;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }

    @Override
    @Transactional
    public boolean shipOrderItem(Long orderId, Long itemId, Long sellerId, String expressCompany, String trackingNumber, String shipProof) {
        OrderItem item = orderItemMapper.selectById(itemId);
        if (item == null) return false;
        if (item.getOrderId() == null || !item.getOrderId().equals(orderId)) return false;
        if (item.getSellerId() == null || !item.getSellerId().equals(sellerId)) throw new SecurityException("无权限对该商品发货");
        if (item.getReceived() != null && item.getReceived() == 1) throw new IllegalStateException("商品已收货，不能发货");
        if (item.getShipped() != null && item.getShipped() == 1) return false;
        Map<String,Object> mp = new HashMap<>();
        mp.put("itemId", itemId);
        mp.put("expressCompany", expressCompany);
        mp.put("trackingNumber", trackingNumber);
        mp.put("shipProof", shipProof);
        int updated = orderItemMapper.markItemShipped(mp);
        if (updated <= 0) return false;
        try {
            Orders order = ordersMapper.selectById(orderId);
            if (order != null) {
                List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
                boolean allShipped = items != null && items.stream().allMatch(it -> it.getShipped() != null && it.getShipped() == 1);
                if (allShipped && "PAID".equals(order.getStatus())) {
                    Orders u = new Orders();
                    u.setId(orderId);
                    u.setStatus("SHIPPED");
                    ordersMapper.updateStatus(u);
                }
            }
        } catch (Exception e) { /* ignore */ }
        return true;
    }
    @Override
    public Map<String, Object> statsByBuyer(Long buyerId) {
        Map<String, Object> m = ordersMapper.statsByBuyer(buyerId);
        if (m == null) {
            m = new HashMap<>();
            m.put("awaitingPayment", 0);
            m.put("awaitingShip", 0);
            m.put("completed", 0);
        }
        return m;
    }
    @Override
    public Map<String, Object> listRelatedOrders(Long userId, String status, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        int offset = (page - 1) * pageSize;
        params.put("userId", userId);
        params.put("status", status);
        params.put("offset", offset);
        params.put("limit", pageSize);

        List<Orders> orders = ordersMapper.listRelatedOrders(params);
        int total = ordersMapper.countRelatedOrders(params);

        List<Orders> out = new ArrayList<>();
        if (orders != null && !orders.isEmpty()) {
            for (Orders o : orders) {
                // 对 items 做不同处理：若当前 user 是买家（o.buyerId == userId）返回全单 items；若为卖家只返回该卖家相关 items
                List<OrderItem> items;
                if (Objects.equals(o.getBuyerId(), userId)) {
                    // 买家：返回整单 items（或可按需过滤）
                    items = orderItemMapper.selectByOrderId(o.getId());
                } else {
                    // 卖家：仅返回该 seller 自己的 item
                    Map<String, Object> p2 = new HashMap<>();
                    p2.put("orderId", o.getId());
                    p2.put("userId", userId);
                    items = orderItemMapper.selectByOrderIdAndSeller(p2);
                }
                if (items == null) items = Collections.emptyList();
                // 处理封面截取等逻辑（和你的其他 list 方法保持一致）
                for (OrderItem it : items) {
                    if ((it.getCover() == null || it.getCover().isEmpty()) && it.getCoverUrls() != null && !it.getCoverUrls().isEmpty()) {
                        String[] arr = it.getCoverUrls().split(",");
                        if (arr.length > 0) it.setCover(arr[0].trim());
                    }
                }
                if (!items.isEmpty()) {
                    o.setItems(items);
                    if (o.getAddressId() != null) o.setAddress(orderAddressMapper.selectById(o.getAddressId()));
                    // 设置 roleForCurrentUser 以便前端渲染
                    if (Objects.equals(o.getBuyerId(), userId)) {
                        o.setRoleForCurrentUser("BUYER");
                    } else {
                        o.setRoleForCurrentUser("SELLER");
                    }
                    out.add(o);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", out);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String,Object> listOrdersForSeller(Long sellerId, String status, Integer shipped, int page, int pageSize) {
        System.out.println("DEBUG Service listOrdersForSeller: sellerId=" + sellerId + ", status=" + status + ", shipped=" + shipped + ", page=" + page + ", pageSize=" + pageSize);

        int offset = (page - 1) * pageSize;
        Map<String,Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        params.put("status", status);
        params.put("offset", offset);
        params.put("limit", pageSize);

        System.out.println("DEBUG params for seller list: " + params);
        System.out.println("DEBUG offset/limit = " + offset + " / " + pageSize);

        // listBySeller will respect status and p2 shipped when provided
        List<Orders> items = ordersMapper.listBySeller(params);
        System.out.println("DEBUG ordersMapper.listBySeller returned raw items (first try) = " + (items == null ? "null" : ("size=" + items.size())));
        if (items == null || items.isEmpty()) {
            try {
                Thread.sleep(80);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            try {
                items = ordersMapper.listBySeller(params);
                System.out.println("DEBUG ordersMapper.listBySeller returned raw items (second try) = " + (items == null ? "null" : ("size=" + items.size())));
            } catch (Exception ex) {
                System.out.println("DEBUG retry listBySeller failed: " + ex.getMessage());
            }
        }

        int total = ordersMapper.countBySeller(params);
        System.out.println("DEBUG after countBySeller -> total = " + total);

        List<Orders> outOrders = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (Orders ord : items) {
                List<OrderItem> orderItems;
                Map<String,Object> p2 = new HashMap<>();
                p2.put("orderId", ord.getId());
                if (shipped != null) p2.put("shipped", shipped);

                System.out.println("DEBUG orderId=" + ord.getId() + " -> calling selectByOrderId with p2=" + p2);
                if (p2.containsKey("shipped")) {
                    orderItems = orderItemMapper.selectByOrderIdAndShipped(p2);
                } else {
                    orderItems = orderItemMapper.selectByOrderId(ord.getId());
                }
                if (orderItems == null) orderItems = Collections.emptyList();

                for (OrderItem it : orderItems) {
                    if ((it.getCover() == null || it.getCover().isEmpty()) && it.getCoverUrls() != null && !it.getCoverUrls().isEmpty()) {
                        String[] arr = it.getCoverUrls().split(",");
                        if (arr.length > 0) it.setCover(arr[0].trim());
                    }
                    if ((it.getSellerId() == null || it.getSellerId() == 0) && it.getGoodsId() != null) {
                        try {
                            GoodsItem g = goodsMapper.selectById(it.getGoodsId());
                            if (g != null && g.getSellerId() != null) {
                                it.setSellerId(g.getSellerId());
                                System.out.println("DEBUG backfilled sellerId from goods for item " + it.getId() + " -> " + g.getSellerId());
                            }
                        } catch (Exception ex) { /* ignore */ }
                    }
                }

                List<OrderItem> filtered = new ArrayList<>();
                for (OrderItem it : orderItems) {
                    if (it.getSellerId() == null) continue;
                    try {
                        if (!Objects.equals(Long.valueOf(it.getSellerId()), sellerId)) {
                            if (it.getSellerId().longValue() != sellerId.longValue()) continue;
                        }
                    } catch (Exception e) {
                        if (!Objects.equals(it.getSellerId(), sellerId)) continue;
                    }
                    if (shipped != null) {
                        Integer s = it.getShipped() == null ? 0 : it.getShipped();
                        if (s.equals(shipped)) filtered.add(it);
                    } else filtered.add(it);
                }

                if (!filtered.isEmpty()) {
                    ord.setItems(filtered);
                    if (ord.getAddressId() != null) ord.setAddress(orderAddressMapper.selectById(ord.getAddressId()));
                    if (ord.getBuyerId() != null && ord.getBuyerId().equals(sellerId)) ord.setRoleForCurrentUser("BOTH"); else ord.setRoleForCurrentUser("SELLER");
                    outOrders.add(ord);
                }
            }
        } else {
            System.out.println("DEBUG items from listBySeller is empty or null, skipping loop.");
        }

        Map<String,Object> out = new HashMap<>();
        out.put("items", outOrders);
        out.put("total", total);
        out.put("page", page);
        out.put("pageSize", pageSize);
        return out;
    }

    private Object[] buildJdbcArgs(Long sellerId, String status, int offset, int pageSize) {
        if (status != null && !status.isEmpty()) {
            return new Object[]{ sellerId, sellerId, status, offset, pageSize };
        } else {
            return new Object[]{ sellerId, sellerId, offset, pageSize };
        }
    }
}