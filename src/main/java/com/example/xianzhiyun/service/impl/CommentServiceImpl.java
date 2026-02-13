package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.Comment;
import com.example.xianzhiyun.mapper.CommentMapper;
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

    @Override
    public List<Comment> getCommentsByGoodsId(Long goodsId, int offset, int limit) {
        // 简单校验
        if (goodsId == null) {
            return List.of(); // 返回空列表而不是报错
        }
        return commentMapper.selectByGoodsId(goodsId, offset, limit);
    }

    @Override
    public int countByGoodsId(Long goodsId) {
        if (goodsId == null) return 0;
        return commentMapper.countByGoodsId(goodsId);
    }

    @Override
    @Transactional
    // 【关键修改点】：增加 Byte rating 参数，与 CommentService 接口保持一致
    public void addComment(Long goodsId, Long userId, String content, Byte rating) {
        // 1. 参数强校验
        if (goodsId == null || userId == null) {
            logger.error("发布评论失败：参数缺失 goodsId={}, userId={}", goodsId, userId);
            throw new IllegalArgumentException("参数错误：无法识别商品或用户");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        // 2. 构建对象
        Comment c = new Comment();
        c.setGoodsId(goodsId);
        c.setUserId(userId);
        c.setContent(content.trim()); // 去除首尾空格

        // 显式设置 articleId 为 null，表明这是商品评论（防止 Mapper 误判）
        c.setArticleId(null);

        // 3. 设置核心字段
        c.setCreateTime(LocalDateTime.now());
        // 【关键修改点】：使用传入的 rating 参数，如果为 null 则默认给 5 分
        c.setRating(rating != null ? rating : (byte) 5);

        // 4. 插入数据库
        int rows = commentMapper.insert(c);
        if (rows <= 0) {
            logger.error("评论插入数据库失败，goodsId={}", goodsId);
            throw new RuntimeException("评论发布失败，请稍后重试");
        }

        logger.info("用户 {} 对商品 {} 发布了评论，评分为 {}", userId, goodsId, c.getRating());
    }
    // CommentServiceImpl.java
    @Service
    public class CommentServiceImpl implements CommentService {
        @Autowired
        private CommentMapper commentMapper;
        @Autowired
        private GoodsMapper goodsMapper; // 注入 GoodsMapper

        @Override
        @Transactional
        public void addComment(Long goodsId, Long userId, String content, Byte rating) {
            // ... 原有参数校验逻辑 ...

            Comment c = new Comment();
            c.setGoodsId(goodsId);
            c.setUserId(userId);
            c.setContent(content.trim());
            c.setCreateTime(LocalDateTime.now());
            c.setRating(rating != null ? rating : (byte) 5);

            int rows = commentMapper.insert(c);
            if (rows > 0) {
                // 【核心修改】同步增加商品表的评论数（即前端显示的咨询数）
                goodsMapper.updateCommentCount(goodsId, 1);
            } else {
                throw new RuntimeException("评论发布失败");
            }
        }
        // ... 其他方法保持不变 ...
    }
}