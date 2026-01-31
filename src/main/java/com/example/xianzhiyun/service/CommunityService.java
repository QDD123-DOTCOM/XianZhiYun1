package com.example.xianzhiyun.service;

import com.example.xianzhiyun.dto.ArticleDTO;
import com.example.xianzhiyun.dto.CommunityDTO;
import com.example.xianzhiyun.dto.CommunityDetailDTO;
import com.example.xianzhiyun.dto.CommunityRecommendDTO; // 新增导入
import com.example.xianzhiyun.entity.CommunityArticleComment;

import java.util.List;

public interface CommunityService {

    // === 社区基础功能 ===
    List<CommunityDTO> listCommunities(String categoryKey, int page, int pageSize);
    int countCommunities(String categoryKey);
    CommunityDetailDTO getCommunityDetail(Long id, Long userId);
    boolean joinCommunity(Long userId, Long communityId);
    boolean leaveCommunity(Long userId, Long communityId);

    // === 帖子相关功能 ===
    // 获取帖子列表 (增加 userId 用于判断是否点赞等状态)
    List<ArticleDTO> getTopics(Long communityId, String type, int page, int pageSize, Long userId);

    // 获取帖子详情
    ArticleDTO getArticleDetail(Long articleId, Long userId);

    // 发布帖子
    Long publishArticle(ArticleDTO articleDTO);

    // 点赞/取消点赞
    boolean toggleLikeArticle(Long userId, Long articleId);

    // === 评论相关功能 ===
    // 获取帖子的评论列表 (使用 CommunityArticleComment 实体)
    List<CommunityArticleComment> getArticleComments(Long articleId);

    // 发表评论
    void addArticleComment(Long userId, Long articleId, String content);

    // === 新增：商品推荐社区功能 ===
    /**
     * 根据商品ID获取推荐社区
     * @param goodsId 商品ID
     * @param userId 当前用户ID (用于判断是否已加入)
     * @return 推荐社区信息DTO
     */
    CommunityRecommendDTO getRecommendedCommunityByGoodsId(Long goodsId, Long userId);
}