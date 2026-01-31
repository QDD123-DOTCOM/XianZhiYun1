package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {
    CartItem selectById(@Param("id") Long id);
    CartItem selectByUserAndGoods(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
    List<CartItem> selectByUserId(@Param("userId") Long userId);
    int insert(CartItem item);
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int updateQuantityByUserAndGoods(@Param("userId") Long userId, @Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);
    int deleteById(@Param("id") Long id);
    int deleteByUserAndGoods(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    // 新增：批量按 goodsId 删除当前用户的购物车项
    int deleteByUserAndGoodsIds(@Param("userId") Long userId, @Param("goodsIds") List<Long> goodsIds);
}