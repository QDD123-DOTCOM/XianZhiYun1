package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.CartItem;
import com.example.xianzhiyun.mapper.CartItemMapper;
import com.example.xianzhiyun.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public List<CartItem> listByUser(Long userId) {
        return cartItemMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void addOrUpdate(Long userId, Long goodsId, Integer quantity) {
        CartItem exist = cartItemMapper.selectByUserAndGoods(userId, goodsId);
        if (exist != null) {
            int current = exist.getQuantity() == null ? 0 : exist.getQuantity();
            int newQty = current + (quantity == null ? 1 : quantity);
            if (newQty < 1) newQty = 1;
            cartItemMapper.updateQuantityByUserAndGoods(userId, goodsId, newQty);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setGoodsId(goodsId);
            item.setQuantity((quantity == null || quantity < 1) ? 1 : quantity);
            cartItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long goodsId, Integer quantity) {
        if (quantity == null || quantity < 1) quantity = 1;
        CartItem exist = cartItemMapper.selectByUserAndGoods(userId, goodsId);
        if (exist != null) {
            cartItemMapper.updateQuantityByUserAndGoods(userId, goodsId, quantity);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setGoodsId(goodsId);
            item.setQuantity(quantity);
            cartItemMapper.insert(item);
        }
    }

    @Override
    public void remove(Long userId, Long goodsId) {
        cartItemMapper.deleteByUserAndGoods(userId, goodsId);
    }

    @Override
    public void removeById(Long userId, Long id) {
        CartItem exist = cartItemMapper.selectById(id);
        if (exist != null && exist.getUserId().equals(userId)) {
            cartItemMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void removeByUserAndGoodsIds(Long userId, List<Long> goodsIds) {
        if (userId == null || goodsIds == null || goodsIds.isEmpty()) return;
        cartItemMapper.deleteByUserAndGoodsIds(userId, goodsIds);
    }
}