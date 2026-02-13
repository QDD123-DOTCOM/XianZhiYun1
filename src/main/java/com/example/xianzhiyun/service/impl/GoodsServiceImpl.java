package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.GoodsPublishDTO;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.mapper.GoodsMapper;
import com.example.xianzhiyun.mapper.GoodsItemMapper;
import com.example.xianzhiyun.service.GoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsItemMapper goodsItemMapper;

    private static final Map<String, List<String>> FILTER_KEYWORD_MAP = new HashMap<>();
    static {
        FILTER_KEYWORD_MAP.put("official", List.of("手办","挂件","海报","T恤","徽章","周边","官方"));
        FILTER_KEYWORD_MAP.put("limited", List.of("限定","特典","首发","签名","限量"));
        FILTER_KEYWORD_MAP.put("collectible", List.of("卡牌","纪念币","画册","原画","收藏","藏品"));
        FILTER_KEYWORD_MAP.put("practical", List.of("包","文具","杯子","手机壳","实用"));
        FILTER_KEYWORD_MAP.put("diy", List.of("同人","手工","定制","DIY"));
        FILTER_KEYWORD_MAP.put("digital", List.of("账号","道具","下载码","数字","激活码"));
        FILTER_KEYWORD_MAP.put("ticket", List.of("门票","票","演唱会","展会"));
        FILTER_KEYWORD_MAP.put("device", List.of("主机","游戏机","耳机","手柄","相机","摄影","设备"));
    }

    @Override
    public List<GoodsItem> listGoods(String categoryKey, String keyword, int page, int pageSize) {
        return listGoods(categoryKey, keyword, null, page, pageSize);
    }

    @Override
    public int countGoods(String categoryKey, String keyword) {
        return countGoods(categoryKey, keyword, null);
    }

    private String normalizeCategory(String key) {
        if (key == null) return null;
        key = key.trim();
        if (key.isEmpty()) return null;
        switch (key) {
            case "数码": return "电子";
            case "电动车": return "运动";
            default: return key;
        }
    }

    @Override
    public List<GoodsItem> getMyGoods(Long sellerId, int page, int size) {
        int offset = (Math.max(page, 1) - 1) * size;
        return goodsMapper.selectBySellerId(offset, size, sellerId);
    }

    @Override
    public int countMyGoods(Long sellerId) {
        return goodsMapper.countBySellerId(sellerId);
    }

    @Override
    @Transactional
    public GoodsItem getById(Long id) {
        GoodsItem item = goodsMapper.selectById(id);
        if (item != null) {
            // 【核心修改】：获取详情时增加浏览量
            goodsMapper.incrementViewCount(id);
            // 同步更新当前对象的数值返回
            item.setViewCount((item.getViewCount() == null ? 0 : item.getViewCount()) + 1);
        }
        return item;
    }

    @Override
    @Transactional
    public Long publish(Long sellerId, GoodsPublishDTO dto) {
        GoodsItem goods = new GoodsItem();
        goods.setSellerId(sellerId);
        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setCategory(dto.getCategory());
        goods.setItemType(dto.getItemType());
        List<String> finalCoverUrls = new ArrayList<>();
        if (dto.getCoverUrls() != null && !dto.getCoverUrls().isEmpty()) {
            finalCoverUrls.addAll(dto.getCoverUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .collect(Collectors.toList()));
        }
        if (dto.getCoverUrl() != null && !dto.getCoverUrl().trim().isEmpty()) {
            String singleUrl = dto.getCoverUrl().trim();
            if (!finalCoverUrls.contains(singleUrl)) {
                finalCoverUrls.add(singleUrl);
            }
        }
        if (finalCoverUrls.isEmpty()) {
            throw new IllegalArgumentException("请至少上传一张商品图片");
        }
        goods.setCoverUrls(String.join(",", finalCoverUrls));
        goods.setStatus("PENDING");
        goods.setIsDeleted(0);

        // 初始化统计字段
        goods.setViewCount(0);
        goods.setFavCount(0);
        goods.setChatCount(0);
        goods.setCommentCount(0);

        goodsMapper.insert(goods);
        return goods.getId();
    }

    @Override
    @Transactional
    public void update(Long id, GoodsPublishDTO dto) {
        GoodsItem goods = goodsMapper.selectById(id);
        if (goods == null) throw new RuntimeException("商品不存在");

        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setCategory(dto.getCategory());
        goods.setItemType(dto.getItemType());

        List<String> finalCoverUrls = new ArrayList<>();
        if (dto.getCoverUrls() != null && !dto.getCoverUrls().isEmpty()) {
            finalCoverUrls.addAll(dto.getCoverUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .collect(Collectors.toList()));
        }
        if (dto.getCoverUrl() != null && !dto.getCoverUrl().trim().isEmpty()) {
            String singleUrl = dto.getCoverUrl().trim();
            if (!finalCoverUrls.contains(singleUrl)) {
                finalCoverUrls.add(singleUrl);
            }
        }
        if (finalCoverUrls.isEmpty()) throw new IllegalArgumentException("请至少上传一张商品图片");
        goods.setCoverUrls(String.join(",", finalCoverUrls));

        if ("ON_SALE".equals(goods.getStatus())) {
            goods.setStatus("PENDING");
            logger.info("商品ID {} 被卖家更新，状态重置为 PENDING 待审核。", id);
        }
        goodsMapper.update(goods);
    }

    @Override
    @Transactional
    public void delete(Long id, Long operatorId) {
        GoodsItem g = goodsMapper.selectById(id);
        if (g == null) throw new IllegalArgumentException("商品不存在: id=" + id);
        if (g.getSellerId() == null || !g.getSellerId().equals(operatorId)) {
            throw new SecurityException("无权限删除该商品");
        }
        goodsMapper.logicalDelete(id);
    }

    @Override
    public void delete(Long id) {
        goodsMapper.logicalDelete(id);
    }

    @Override
    public void resubmit(Long id) {
        GoodsItem g = goodsMapper.selectById(id);
        if (g != null) {
            g.setStatus("PENDING");
            goodsMapper.update(g);
        }
    }

    @Override
    public List<GoodsItem> getGoodsBySeller(Long sellerId, int offset, int limit) {
        return goodsMapper.selectBySellerId(offset, limit, sellerId);
    }

    @Override
    public int countBySellerId(Long sellerId) {
        return goodsMapper.countBySellerId(sellerId);
    }

    @Override
    public Map<Long, GoodsItem> mapByIds(List<Long> ids) {
        Map<Long, GoodsItem> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) return map;
        List<GoodsItem> list = goodsMapper.selectByIds(ids);
        if (list != null) {
            for (GoodsItem g : list) {
                if (g != null && g.getId() != null) map.put(g.getId(), g);
            }
        }
        return map;
    }

    @Override
    public List<GoodsItem> listGoods(String categoryKey, String keyword, String filterType, int page, int pageSize) {
        int offset = (Math.max(page, 1) - 1) * pageSize;
        String normalizedCategory = normalizeCategory(categoryKey);
        String normalizedKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        Map<String, Object> params = new HashMap<>();
        params.put("category", normalizedCategory);
        params.put("keyword", normalizedKeyword);
        params.put("offset", offset);
        params.put("limit", pageSize);
        if (filterType != null && !filterType.trim().isEmpty()) {
            String ft = filterType.trim();
            params.put("itemType", ft);
            List<String> kws = FILTER_KEYWORD_MAP.getOrDefault(ft, List.of(ft));
            params.put("filterKeywords", kws);
        }
        return goodsMapper.selectGoodsList(params);
    }

    @Override
    public void delist(long goodsId, long sellerId){
        GoodsItem g = goodsMapper.selectById(goodsId);
        if (g == null) throw new IllegalArgumentException("商品不存在");
        if (!g.getSellerId().equals(sellerId)) throw new SecurityException("无权限操作");
        this.updateStatus(goodsId,"OFF_SHELF");
    }

    @Override
    public int countGoods(String categoryKey, String keyword, String filterType) {
        String normalizedCategory = normalizeCategory(categoryKey);
        String normalizedKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        Map<String, Object> params = new HashMap<>();
        params.put("category", normalizedCategory);
        params.put("keyword", normalizedKeyword);
        if (filterType != null && !filterType.trim().isEmpty()) {
            String ft = filterType.trim();
            params.put("itemType", ft);
            List<String> kws = FILTER_KEYWORD_MAP.getOrDefault(ft, List.of(ft));
            params.put("filterKeywords", kws);
        }
        return goodsMapper.selectGoodsCount(params);
    }

    @Override
    public List<GoodsItem> listGoodsForAdmin(Map<String, Object> params) {
        return goodsItemMapper.selectAdminList(params);
    }

    @Override
    public int countGoodsForAdmin(Map<String, Object> params) {
        return goodsItemMapper.selectAdminCount(params);
    }

    @Override
    public void updateStatus(Long id, String status) {
        GoodsItem g = goodsMapper.selectById(id);
        if (g == null) throw new IllegalArgumentException("商品不存在");
        final java.util.Set<String> ALLOWED = java.util.Set.of("DRAFT", "PENDING", "ON_SALE", "SOLD", "REJECTED", "DELETED","OFF_SHELF");
        if (status == null || !ALLOWED.contains(status)) throw new IllegalArgumentException("Invalid status");
        g.setStatus(status);
        goodsMapper.update(g);
    }
}