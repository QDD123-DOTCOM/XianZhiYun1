package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> listByUser(Long userId);
    void addOrUpdate(Long userId, Long goodsId, Integer quantity);
    void updateQuantity(Long userId, Long goodsId, Integer quantity);
    void remove(Long userId, Long goodsId);
    void removeById(Long userId, Long id);
    /**
     * 批量按 goodsId 删除当前用户的购物车项
     * @param userId 用户ID
     * @param goodsIds 商品ID列表
     */
    void removeByUserAndGoodsIds(Long userId, List<Long> goodsIds);
}