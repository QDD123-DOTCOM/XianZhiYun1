package com.example.xianzhiyun.service;

import com.example.xianzhiyun.dto.GoodsPublishDTO;
import com.example.xianzhiyun.entity.GoodsItem;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    List<GoodsItem> getMyGoods(Long sellerId, int page, int size);
    int countMyGoods(Long sellerId);

    GoodsItem getById(Long id);

    Long publish(Long sellerId, GoodsPublishDTO dto);
    void update(Long id, GoodsPublishDTO dto);

    /**
     * 逻辑删除商品（将 status 设置为 DELETED）
     * operatorId 为发起删除的用户 id（用于权限校验）
     */
    void delete(Long id, Long operatorId);

    /**
     * 兼容旧签名（内部调用新的 delete）
     */
    void delete(Long id);

    void resubmit(Long id);

    // 通用的分页列表（按分类、关键词）
    List<GoodsItem> listGoods(String categoryKey, String keyword, int page, int pageSize);
    int countGoods(String categoryKey, String keyword);

    // 新增：按卖家查询（offset/limit）
    List<GoodsItem> getGoodsBySeller(Long sellerId, int offset, int limit);

    // 新增：统计某个卖家的商品数量
    int countBySellerId(Long sellerId);

    // 新增：根据 ids 批量返回 Map<id, GoodsItem>
    Map<Long, GoodsItem> mapByIds(List<Long> ids);
    // 在 interface GoodsService 中添加
    List<GoodsItem> listGoodsForAdmin(Map<String, Object> params);
    int countGoodsForAdmin(Map<String, Object> params);
    void updateStatus(Long id, String status);
    List<GoodsItem> listGoods(String categoryKey, String keyword, String filterType, int page, int pageSize);

    void delist(long goodsId, long sellerId);

    int countGoods(String categoryKey, String keyword, String filterType);

}