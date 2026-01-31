package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FavoriteMapper {

    Integer countByUserAndGoods(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    Integer countByUserId(@Param("userId") Long userId);

    void insert(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    void delete(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    /**
     * 分页查询，返回包含商品信息的列表
     * 每条 Map 包含: favId, favoritedAt, goodsId, title, price, cover, status, sellerId
     */
    List<Map<String, Object>> selectPageByUserId(@Param("userId") Long userId,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);
}