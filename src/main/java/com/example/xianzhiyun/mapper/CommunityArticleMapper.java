package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.dto.ArticleDTO;
import com.example.xianzhiyun.entity.CommunityArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommunityArticleMapper {

    int insert(CommunityArticle article);

    // 查询单条帖子详情（返回 DTO 以包含作者信息）
    ArticleDTO selectArticleDTOById(@Param("id") Long id);

    // 更新统计数据
    void incrementViewCount(@Param("id") Long id);
    void incrementLikeCount(@Param("id") Long id);
    void decrementLikeCount(@Param("id") Long id);
    void incrementCommentCount(@Param("id") Long id);
    void increaseCommentCount(Long id); // 新增这一行

    // 现有的方法
    int countByCommunityId(@Param("communityId") Long communityId);
    List<CommunityArticle> selectLatestByCommunityIds(@Param("ids") List<Long> ids, @Param("limit") int limit);
    List<ArticleDTO> selectArticleDTOList(@Param("communityId") Long communityId,
                                          @Param("type") String type,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);
}