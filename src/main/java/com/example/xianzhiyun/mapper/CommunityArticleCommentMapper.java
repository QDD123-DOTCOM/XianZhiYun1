package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.CommunityArticleComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityArticleCommentMapper {
    // 插入评论
    int insert(CommunityArticleComment comment);

    // 根据文章ID查询评论列表（包含用户信息）
    List<CommunityArticleComment> selectByArticleId(@Param("articleId") Long articleId);
}