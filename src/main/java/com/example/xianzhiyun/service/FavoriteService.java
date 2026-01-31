package com.example.xianzhiyun.service;

import java.util.Map;

public interface FavoriteService {
    boolean isFavorited(Long userId, Long goodsId);
    void addFavorite(Long userId, Long goodsId);
    void removeFavorite(Long userId, Long goodsId);
    long countByUserId(Long userId);

    /**
     * 分页获取收藏列表，返回 Map 包含 items (List<Map>) 与 total (int)
     */
    Map<String, Object> pageFavorites(Long userId, int page, int pageSize);
}