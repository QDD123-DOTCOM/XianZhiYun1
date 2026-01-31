package com.example.xianzhiyun.dto;

import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.entity.GoodsItem;
import lombok.*;

import java.util.List;

/**
 * 首页返回对象（不再包含轮播图）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeDTO {
    /** 正在进行的活动（使用 donation_event 表的数据结构） */
    private List<DonationEvent> activities;

    /** 推荐商品（最新上架的 ON_SALE 商品） */
    private List<GoodsItem> recommendGoods;
}