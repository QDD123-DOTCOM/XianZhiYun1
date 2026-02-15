package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.HomeDTO;
import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.mapper.DonationEventMapper;
import com.example.xianzhiyun.mapper.GoodsMapper;
import com.example.xianzhiyun.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

    private final DonationEventMapper donationEventMapper;
    private final GoodsMapper goodsMapper;

    public HomeServiceImpl(DonationEventMapper donationEventMapper,
                           GoodsMapper goodsMapper) {
        this.donationEventMapper = donationEventMapper;
        this.goodsMapper = goodsMapper;
    }

    @Override
    public HomeDTO getHomeData() {
        HomeDTO dto = new HomeDTO();

        // 1. 获取正在进行的活动（最多 5 条）
        List<DonationEvent> events = donationEventMapper.selectOngoing();
        List<DonationEvent> activities = (events == null) ? java.util.Collections.emptyList()
                : events.stream().limit(5).collect(Collectors.toList());
        dto.setActivities(activities);

        // 2. 获取推荐商品（取 8 条最新上架商品）
        List<GoodsItem> recommendRaw = goodsMapper.selectRecommend(8);

        // 3. 将商品实体转换为包含统计字段的 Map 列表
        List<Map<String, Object>> recommendedGoodsMaps = new ArrayList<>();
        if (recommendRaw != null) {
            recommendedGoodsMaps = recommendRaw.stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());
        }
        dto.setRecommendGoods(recommendedGoodsMaps);

        logger.info("首页数据加载完成：活动 {} 条，推荐商品 {} 条", activities.size(), recommendedGoodsMaps.size());
        return dto;
    }

    /**
     * 辅助方法：将 GoodsItem 转换为前端需要的 Map 格式
     * 包含真实的统计数据：浏览量、收藏数、评论数
     */
    private Map<String, Object> convertToMap(GoodsItem g) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", g.getId());
        m.put("sellerId", g.getSellerId());
        m.put("title", g.getTitle());
        m.put("description", g.getDescription());
        m.put("price", g.getPrice());
        m.put("category", g.getCategory());
        m.put("itemType", g.getItemType());
        m.put("status", g.getStatus());
        m.put("createTime", g.getCreateTime());
        m.put("updateTime", g.getUpdateTime());

        // 映射统计字段
        m.put("viewCount", g.getViewCount() != null ? g.getViewCount() : 0);
        m.put("favCount", g.getFavCount() != null ? g.getFavCount() : 0);
        m.put("commentCount", g.getCommentCount() != null ? g.getCommentCount() : 0);
        m.put("chatCount", g.getCommentCount() != null ? g.getCommentCount() : 0); // 咨询数取评论数

        // 处理图片 URL 列表
        String cvs = g.getCoverUrls();
        List<String> list = new ArrayList<>();
        if (cvs != null && !cvs.trim().isEmpty()) {
            list = Arrays.stream(cvs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        m.put("coverUrls", list);
        m.put("coverUrl", list.isEmpty() ? "" : list.get(0));

        return m;
    }
}