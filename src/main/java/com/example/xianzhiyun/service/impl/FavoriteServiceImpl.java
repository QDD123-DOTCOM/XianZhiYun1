package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.mapper.FavoriteMapper;
import com.example.xianzhiyun.mapper.GoodsMapper;
import com.example.xianzhiyun.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private GoodsMapper goodsMapper; // 注入 GoodsMapper 用于同步更新计数

    @Override
    public boolean isFavorited(Long userId, Long goodsId) {
        Integer cnt = favoriteMapper.countByUserAndGoods(userId, goodsId);
        return cnt != null && cnt > 0;
    }

    @Override
    @Transactional
    public void addFavorite(Long userId, Long goodsId) {
        if (!isFavorited(userId, goodsId)) {
            favoriteMapper.insert(userId, goodsId);
            // 【核心修改】：收藏成功，商品表收藏数 +1
            goodsMapper.updateFavCount(goodsId, 1);
        }
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long goodsId) {
        if (isFavorited(userId, goodsId)) {
            favoriteMapper.delete(userId, goodsId);
            // 【核心修改】：取消收藏，商品表收藏数 -1
            goodsMapper.updateFavCount(goodsId, -1);
        }
    }

    @Override
    public long countByUserId(Long userId) {
        Integer cnt = favoriteMapper.countByUserId(userId);
        return cnt == null ? 0L : cnt.longValue();
    }

    @Override
    public Map<String, Object> pageFavorites(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> items = favoriteMapper.selectPageByUserId(userId, offset, pageSize);
        int total = favoriteMapper.countByUserId(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("items", items);
        res.put("total", total);
        return res;
    }
}