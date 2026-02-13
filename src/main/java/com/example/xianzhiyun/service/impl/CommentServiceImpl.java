package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.Comment;
import com.example.xianzhiyun.mapper.CommentMapper;
import com.example.xianzhiyun.mapper.GoodsMapper;
import com.example.xianzhiyun.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private GoodsMapper goodsMapper; // 注入 GoodsMapper 用于同步更新计数

    @Override
    public List<Comment> getCommentsByGoodsId(Long goodsId, int offset, int limit) {
        if (goodsId == null) return List.of();
        return commentMapper.selectByGoodsId(goodsId, offset, limit);
    }

    @Override
    public int countByGoodsId(Long goodsId) {
        if (goodsId == null) return 0;
        return commentMapper.countByGoodsId(goodsId);
    }

    @Override
    @Transactional
    public void addComment(Long goodsId, Long userId, String content, Byte rating) {
        if (goodsId == null || userId == null) {
            throw new IllegalArgumentException("参数错误：无法识别商品或用户");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        Comment c = new Comment();
        c.setGoodsId(goodsId);
        c.setUserId(userId);
        c.setContent(content.trim());
        c.setArticleId(null);
        c.setCreateTime(LocalDateTime.now());
        c.setRating(rating != null ? rating : (byte) 5);

        int rows = commentMapper.insert(c);
        if (rows <= 0) {
            throw new RuntimeException("评论发布失败");
        }

        // 【核心修改】：评论成功后，同步增加商品表的评论数（咨询数）
        goodsMapper.updateCommentCount(goodsId, 1);

        logger.info("用户 {} 对商品 {} 发布了咨询/评论", userId, goodsId);
    }
}