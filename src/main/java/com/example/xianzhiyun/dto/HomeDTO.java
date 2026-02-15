package com.example.xianzhiyun.dto;

import com.example.xianzhiyun.entity.DonationEvent;
import lombok.Data;

import java.util.List;
import java.util.Map; // 导入 Map

/**
 * 首页数据传输对象
 */
@Data
public class HomeDTO {
    private List<DonationEvent> activities;

    // 【核心修改】：现在推荐商品是 List<Map<String, Object>> 类型
    private List<Map<String, Object>> recommendGoods;
}