package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.ArticleDTO;
import com.example.xianzhiyun.dto.CommunityDTO;
import com.example.xianzhiyun.dto.CommunityDetailDTO;
import com.example.xianzhiyun.dto.CommunityRecommendDTO; // 新增导入
import com.example.xianzhiyun.entity.*;
import com.example.xianzhiyun.mapper.*;
import com.example.xianzhiyun.service.CommunityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils; // 新增导入
import org.springframework.util.StringUtils; // 新增导入

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // 新增导入

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private CommunityMemberMapper memberMapper;

    @Autowired
    private CommunityArticleMapper articleMapper;

    @Autowired
    private CommunityArticleCommentMapper communityArticleCommentMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private GoodsItemMapper goodsItemMapper; // 注入 GoodsItemMapper
    @Autowired
    private GoodsCommunityCategoryMapMapper goodsCommunityCategoryMapMapper; // 注入新的 Mapper

    // ================= 社区列表与详情 =================

    @Override
    public List<CommunityDTO> listCommunities(String categoryKey, int page, int pageSize) {
        // 处理 "推荐" 或 "全部" 标签，转换为 null 以查询所有
        if ("推荐".equals(categoryKey) || "全部".equals(categoryKey)) {
            categoryKey = null;
        }

        int offset = (page - 1) * pageSize;
        List<Community> list = communityMapper.selectByCategory(categoryKey, offset, pageSize);

        List<CommunityDTO> dtos = new ArrayList<>();
        for (Community c : list) {
            CommunityDTO dto = new CommunityDTO();
            BeanUtils.copyProperties(c, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public int countCommunities(String categoryKey) {
        if ("推荐".equals(categoryKey) || "全部".equals(categoryKey)) {
            categoryKey = null;
        }
        return communityMapper.countByCategory(categoryKey);
    }

    @Override
    public CommunityDetailDTO getCommunityDetail(Long id, Long userId) {
        Community community = communityMapper.selectById(id);
        if (community == null) return null;

        CommunityDetailDTO dto = new CommunityDetailDTO();
        BeanUtils.copyProperties(community, dto);

        boolean isJoined = false;
        String role = "GUEST";
        if (userId != null) {
            CommunityMember member = memberMapper.selectByCommunityAndUser(id, userId);
            if (member != null) {
                isJoined = true;
                role = member.getRole();
            }
        }
        dto.setJoined(isJoined);
        dto.setMyRole(role);

        return dto;
    }

    // ================= 加入与退出 =================

    @Override
    @Transactional
    public boolean joinCommunity(Long userId, Long communityId) {
        CommunityMember exist = memberMapper.selectByCommunityAndUser(communityId, userId);
        if (exist != null) return true;

        CommunityMember member = new CommunityMember();
        member.setCommunityId(communityId);
        member.setUserId(userId);
        member.setRole("MEMBER");
        // 社区成员表通常使用 LocalDateTime
        member.setJoinTime(Timestamp.valueOf(LocalDateTime.now()));

        int rows = memberMapper.insert(member);
        if (rows > 0) {
            communityMapper.incrementMemberCount(communityId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean leaveCommunity(Long userId, Long communityId) {
        int rows = memberMapper.deleteByCommunityAndUser(communityId, userId);
        if (rows > 0) {
            communityMapper.decrementMemberCount(communityId);
            return true;
        }
        return false;
    }

    // ================= 帖子相关 =================

    @Override
    public List<ArticleDTO> getTopics(Long communityId, String type, int page, int pageSize, Long userId) {
        int offset = (page - 1) * pageSize;
        return articleMapper.selectArticleDTOList(communityId, type, offset, pageSize);
    }

    @Override
    @Transactional
    public ArticleDTO getArticleDetail(Long articleId, Long userId) {
        articleMapper.incrementViewCount(articleId);
        // 注意：这里需要确保 articleMapper.selectArticleDTOById 能够处理 userId 来判断点赞状态
        // 如果 ArticleDTO 中没有 isLiked 字段，或者该方法没有处理，则需要修改 ArticleDTO 和 ArticleMapper
        ArticleDTO dto = articleMapper.selectArticleDTOById(articleId);
        return dto;
    }

    @Override
    @Transactional
    public Long publishArticle(ArticleDTO dto) {
        CommunityArticle article = new CommunityArticle();
        BeanUtils.copyProperties(dto, article);

        article.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        article.setStatus("PUBLISHED");
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);

        articleMapper.insert(article);
        communityMapper.incrementTopicCount(dto.getCommunityId());

        return article.getId();
    }

    @Override
    @Transactional
    public boolean toggleLikeArticle(Long userId, Long articleId) {
        // 这里只是简单地增加点赞数，没有处理重复点赞或取消点赞的逻辑
        // 如果需要完整的点赞/取消点赞功能，需要一个单独的点赞记录表 (community_article_likes)
        // 并根据是否存在记录来决定是增加还是减少点赞数
        articleMapper.incrementLikeCount(articleId);
        return true;
    }

    // ================= 评论相关 =================

    @Override
    public List<CommunityArticleComment> getArticleComments(Long articleId) {
        return communityArticleCommentMapper.selectByArticleId(articleId);
    }

    @Override
    @Transactional
    public void addArticleComment(Long userId, Long articleId, String content) {
        CommunityArticleComment comment = new CommunityArticleComment();
        comment.setUserId(userId);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now()); // 使用 LocalDateTime

        communityArticleCommentMapper.insert(comment);
        articleMapper.incrementCommentCount(articleId);
    }

    // ================= 新增：商品推荐社区功能实现 =================

    @Override
    public CommunityRecommendDTO getRecommendedCommunityByGoodsId(Long goodsId, Long userId) {
        if (goodsId == null) {
            return getDefaultCommunityDTO(userId); // 如果没有商品ID，返回默认社区
        }

        // 1. 获取商品信息
        GoodsItem goodsItem = goodsItemMapper.selectById(goodsId);
        if (goodsItem == null || StringUtils.isEmpty(goodsItem.getCategory())) {
            return getDefaultCommunityDTO(userId); // 如果商品不存在或无分类，返回默认社区
        }

        String goodsCategoryName = goodsItem.getCategory();
        Community community = null;

        // 2. 根据商品分类名称查找映射关系
        List<GoodsCommunityCategoryMap> maps = goodsCommunityCategoryMapMapper.selectByGoodsCategoryName(goodsCategoryName);

        if (!CollectionUtils.isEmpty(maps)) {
            // 优先查找指定了 default_community_id 的映射
            Optional<GoodsCommunityCategoryMap> defaultMap = maps.stream()
                    .filter(m -> m.getDefaultCommunityId() != null)
                    .findFirst();

            if (defaultMap.isPresent()) {
                community = communityMapper.selectById(defaultMap.get().getDefaultCommunityId());
            } else {
                // 如果没有指定默认社区，则根据 community_category_key 查找
                // 这里选择第一个匹配的 community_category_key，然后查找该分类下成员数最多的社区
                String communityCategoryKey = maps.get(0).getCommunityCategoryKey();

                // 假设 communityMapper 有一个方法根据 category_key 查找社区，并按 member_count 降序
                // 你的 CommunityMapper.xml 中 selectByCategory 已经有 ORDER BY member_count DESC
                // 可以复用 selectByCategory，但需要传入 null 作为 offset 和 limit 来获取所有匹配的
                List<Community> communities = communityMapper.selectByCommunityCategoryKey(communityCategoryKey);
                if (!CollectionUtils.isEmpty(communities)) {
                    community = communities.get(0); // 取成员数最多的社区
                }
            }
        }

        // 3. 如果通过映射找到了社区，或者直接通过分类找到了社区
        if (community == null) {
            community = getDefaultCommunity(userId); // 如果没找到，返回默认社区
        }

        // 4. 组装 DTO
        CommunityRecommendDTO dto = new CommunityRecommendDTO();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setIcon(community.getCoverUrl());
        dto.setMemberCount(community.getMemberCount() != null ? community.getMemberCount() : 0);

        // 5. 判断用户是否已加入
        if (userId != null && community.getId() != null) {
            // 使用 memberMapper.exists 方法来判断用户是否已加入社区
            boolean isJoined = memberMapper.exists(community.getId(), userId) > 0;
            dto.setIsJoined(isJoined);
        } else {
            dto.setIsJoined(false);
        }

        return dto;
    }

    /**
     * 获取一个默认的通用社区，作为兜底方案
     */
    private Community getDefaultCommunity(Long userId) {
        // 例如，返回 ID 为 301 的社区，或者查找一个 memberCount 最高的通用社区
        Community defaultCommunity = communityMapper.selectById(301L); // 假设 301 是你的一个通用推荐社区
        if (defaultCommunity == null) {
            // 如果连默认社区都找不到，可以创建一个临时的或者返回null
            defaultCommunity = new Community(); // 填充一些默认值
            defaultCommunity.setId(0L); // 0表示一个虚拟的、不存在的社区ID，前端可以据此判断不显示加入按钮
            defaultCommunity.setName("通用交流区");
            defaultCommunity.setDescription("欢迎来到通用交流区，这里有各种好物分享。");
            // 假设你有一个默认的图标URL，请替换为你的实际URL
            defaultCommunity.setCoverUrl("http://192.168.88.46:8088/static/upload/default_community_icon.png");
            defaultCommunity.setMemberCount(1000); // 默认成员数
        }
        return defaultCommunity;
    }

    /**
     * 将 Community 实体转换为 CommunityRecommendDTO
     */
    private CommunityRecommendDTO getDefaultCommunityDTO(Long userId) {
        Community defaultCommunity = getDefaultCommunity(userId);
        CommunityRecommendDTO dto = new CommunityRecommendDTO();
        dto.setId(defaultCommunity.getId());
        dto.setName(defaultCommunity.getName());
        dto.setDescription(defaultCommunity.getDescription());
        dto.setIcon(defaultCommunity.getCoverUrl());
        dto.setMemberCount(defaultCommunity.getMemberCount());
        // 默认社区通常不会认为用户已加入，除非你有一个特殊的“默认社区”加入逻辑
        dto.setIsJoined(false);
        return dto;
    }
}