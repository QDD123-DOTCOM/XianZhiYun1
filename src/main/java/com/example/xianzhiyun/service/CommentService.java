package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByGoodsId(Long goodsId, int offset, int limit);
    int countByGoodsId(Long goodsId);
    void addComment(Long goodsId, Long userId, String content, Byte rating);
}