package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectByGoodsId(@Param("goodsId") Long goodsId, @Param("offset") int offset, @Param("limit") int limit);
    int countByGoodsId(@Param("goodsId") Long goodsId);
    int insert(Comment comment);

    List<Comment> selectByArticleId(Long articleId);
}