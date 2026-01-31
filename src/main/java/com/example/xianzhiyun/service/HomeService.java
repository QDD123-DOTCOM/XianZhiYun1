package com.example.xianzhiyun.service;

/**
 * 首页业务层接口
 */
public interface HomeService {

    /**
     * 查询首页需要的所有数据
     *
     * @return 包含活动 + 推荐商品的聚合对象
     */
    com.example.xianzhiyun.dto.HomeDTO getHomeData();
}