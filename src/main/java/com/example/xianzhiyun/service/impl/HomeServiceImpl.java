package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.HomeDTO;
import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.mapper.DonationEventMapper;
import com.example.xianzhiyun.mapper.GoodsItemMapper;
import com.example.xianzhiyun.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

    private final DonationEventMapper donationEventMapper;
    private final GoodsItemMapper goodsItemMapper;

    public HomeServiceImpl(DonationEventMapper donationEventMapper,
                           GoodsItemMapper goodsItemMapper) {
        this.donationEventMapper = donationEventMapper;
        this.goodsItemMapper = goodsItemMapper;
    }

    @Override
    public HomeDTO getHomeData() {
        HomeDTO dto = new HomeDTO();

        // 1. 正在进行的活动（最多 5 条）
        List<DonationEvent> events = donationEventMapper.selectOngoing();
        int eventsSize = events == null ? 0 : events.size();
        logger.info("DEBUG HomeServiceImpl.selectOngoing returned size={}", eventsSize);
        if (events != null && !events.isEmpty()) {
            for (DonationEvent e : events) {
                try {
                    logger.info("DEBUG event id={} title={} status={} startTime={} endTime={}",
                            e.getId(), e.getTitle(), e.getStatus(), e.getStartTime(), e.getEndTime());
                } catch (Exception ex) {
                    logger.warn("DEBUG event toString failed: {}", ex.getMessage());
                }
            }
        }

        List<DonationEvent> activities = (events == null) ? java.util.Collections.emptyList()
                : events.stream().limit(5).collect(Collectors.toList());
        dto.setActivities(activities);

        // 2. 推荐商品（默认取 8 条最新上架的 ON_SALE 商品）
        List<GoodsItem> recommend = goodsItemMapper.selectRecommend(8);
        int recommendSize = recommend == null ? 0 : recommend.size();
        logger.info("DEBUG HomeServiceImpl.selectRecommend returned size={}", recommendSize);
        dto.setRecommendGoods(recommend);

        return dto;
    }
}