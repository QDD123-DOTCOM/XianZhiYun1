package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.CartItem;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.service.CartService;
import com.example.xianzhiyun.service.GoodsService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired(required = false)
    private GoodsService goodsService;

    @Value("${xianzhiyun.imageBase:https://cdn.example.com}")
    private String imageBase;

    /**
     * 获取购物车列表
     */
    @GetMapping
    public JsonResult list(@RequestAttribute("userId") Long userId) {
        List<CartItem> items = cartService.listByUser(userId);
        List<Map<String, Object>> out = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            return JsonResult.success(out);
        }

        List<Long> ids = new ArrayList<>();
        for (CartItem ci : items) ids.add(ci.getGoodsId());

        Map<Long, GoodsItem> goodsMap = new HashMap<>();
        if (goodsService != null) {
            try {
                goodsMap = goodsService.mapByIds(ids);
            } catch (Exception e) {
                goodsMap = new HashMap<>();
            }
        }

        for (CartItem ci : items) {
            Map<String, Object> m = new HashMap<>();
            m.put("goodsId", ci.getGoodsId());
            GoodsItem g = goodsMap.get(ci.getGoodsId());

            if (g != null) {
                // 【核心修改】：统一使用 coverUrls 和 title
                String firstCover = "";
                String cvs = g.getCoverUrls();
                if (cvs != null && !cvs.isBlank()) {
                    String[] parts = cvs.split(",");
                    if (parts.length > 0) firstCover = parts[0].trim();
                }

                // 规范化 URL
                firstCover = normalizeUrl(firstCover);

                m.put("cover", firstCover);
                m.put("title", g.getTitle() != null ? g.getTitle() : "未知商品");
                m.put("price", g.getPrice());
            } else {
                m.put("cover", "");
                m.put("title", "商品已失效");
                m.put("price", 0);
            }
            m.put("quantity", ci.getQuantity() == null ? 1 : ci.getQuantity());
            out.add(m);
        }

        return JsonResult.success(out);
    }

    /**
     * 获取购物车商品总数
     */
    @GetMapping("/count")
    public JsonResult count(@RequestAttribute("userId") Long userId) {
        List<CartItem> items = cartService.listByUser(userId);
        int count = 0;
        if (items != null) {
            for (CartItem it : items) count += (it.getQuantity() == null ? 0 : it.getQuantity());
        }
        return JsonResult.success(Collections.singletonMap("count", count));
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public JsonResult add(@RequestBody Map<String, Object> body, @RequestAttribute("userId") Long userId) {
        if (body == null || body.get("goodsId") == null) return JsonResult.error(400, "goodsId 必须");
        Long goodsId;
        Integer qty;
        try {
            goodsId = Long.valueOf(String.valueOf(body.get("goodsId")));
        } catch (Exception e) {
            return JsonResult.error(400, "goodsId 格式错误");
        }
        try {
            qty = body.get("quantity") == null ? 1 : Integer.valueOf(String.valueOf(body.get("quantity")));
        } catch (Exception e) {
            qty = 1;
        }
        if (qty < 1) qty = 1;
        cartService.addOrUpdate(userId, goodsId, qty);
        return JsonResult.success(null);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{goodsId}")
    public JsonResult updateQuantity(@PathVariable Long goodsId,
                                     @RequestBody Map<String, Object> body,
                                     @RequestAttribute("userId") Long userId) {
        Integer quantity = 1;
        if (body != null && body.get("quantity") != null) {
            try { quantity = Integer.valueOf(String.valueOf(body.get("quantity"))); } catch (Exception e) { quantity = 1; }
        }
        if (quantity < 1) quantity = 1;
        cartService.updateQuantity(userId, goodsId, quantity);
        return JsonResult.success(null);
    }

    /**
     * 从购物车移除单个商品
     */
    @DeleteMapping("/{goodsId}")
    public JsonResult delete(@PathVariable Long goodsId, @RequestAttribute("userId") Long userId) {
        cartService.remove(userId, goodsId);
        return JsonResult.success(null);
    }

    /**
     * 批量移除购物车商品
     */
    @DeleteMapping
    public JsonResult clearByGoodsIds(@RequestParam(value = "goodsIds", required = false) String goodsIdsCsv,
                                      @RequestAttribute("userId") Long userId) {
        if (goodsIdsCsv == null || goodsIdsCsv.trim().isEmpty()) {
            return JsonResult.error(400, "goodsIds 不能为空");
        }
        String[] parts = goodsIdsCsv.split(",");
        List<Long> ids = new ArrayList<>();
        for (String p : parts) {
            try { ids.add(Long.parseLong(p.trim())); } catch (Exception ignored) {}
        }
        if (!ids.isEmpty()) {
            cartService.removeByUserAndGoodsIds(userId, ids);
            return JsonResult.success(null);
        }
        return JsonResult.success(null);
    }

    private String normalizeUrl(String url) {
        if (url == null) return "";
        String u = url.trim();
        if (u.isEmpty()) return "";
        if (u.startsWith("http://") || u.startsWith("https://")) return u;
        String base = (imageBase == null) ? "" : imageBase.trim();
        if (base.isEmpty()) return u;
        return base.replaceAll("/$", "") + "/" + u.replaceAll("^/+", "");
    }
}
