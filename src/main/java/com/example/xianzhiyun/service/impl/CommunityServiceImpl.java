package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.dto.ArticleDTO;
import com.example.xianzhiyun.dto.CommunityDTO;
import com.example.xianzhiyun.dto.CommunityDetailDTO;
import com.example.xianzhiyun.dto.CommunityRecommendDTO;
import com.example.xianzhiyun.entity.*;
import com.example.xianzhiyun.mapper.*;
import com.example.xianzhiyun.service.CommunityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private UserInfoMapper userInfoMapper; // 虽然这里没有直接使用，但你的代码中存在，所以保留

    @Autowired
    private GoodsItemMapper goodsItemMapper;

    @Autowired
    private GoodsCommunityCategoryMapMapper goodsCommunityCategoryMapMapper;

    @Override
    public List<CommunityDTO> listCommunities(String categoryKey, int page, int pageSize) {
        // 【核心修改】：如果分类是空字符串、"recommend" 或 "全部"，将其设为 null 以便 SQL 查询全表
        if (categoryKey == null || categoryKey.trim().isEmpty() || "recommend".equals(categoryKey) || "全部".equals(categoryKey)) {
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
        // 【核心修改】：逻辑同上
        if (categoryKey == null || categoryKey.trim().isEmpty() || "recommend".equals(categoryKey) || "全部".equals(categoryKey)) {
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

    @Override
    @Transactional
    public boolean joinCommunity(Long userId, Long communityId) {
        CommunityMember exist = memberMapper.selectByCommunityAndUser(communityId, userId);
        if (exist != null) return true;

        CommunityMember member = new CommunityMember();
        member.setCommunityId(communityId);
        member.setUserId(userId);
        member.setRole("MEMBER");
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

    @Override
    public List<CommunityDTO> listMyJoinedCommunities(Long userId) {
        List<Community> list = communityMapper.selectMyJoined(userId);
        List<CommunityDTO> dtos = new ArrayList<>();
        for (Community c : list) {
            CommunityDTO dto = new CommunityDTO();
            BeanUtils.copyProperties(c, dto);
            dto.setJoined(true); // 既然是从加入表查出来的，isJoined 肯定都是 true
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<ArticleDTO> getTopics(Long communityId, String type, int page, int pageSize, Long userId) {
        int offset = (page - 1) * pageSize;
        return articleMapper.selectArticleDTOList(communityId, type, offset, pageSize);
    }

    @Override
    @Transactional
    public ArticleDTO getArticleDetail(Long articleId, Long userId) {
        articleMapper.incrementViewCount(articleId);
        return articleMapper.selectArticleDTOById(articleId);
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
        articleMapper.incrementLikeCount(articleId);
        return true;
    }

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
        comment.setCreatedAt(LocalDateTime.now());

        communityArticleCommentMapper.insert(comment);
        articleMapper.incrementCommentCount(articleId);
    }

    @Override
    public CommunityRecommendDTO getRecommendedCommunityByGoodsId(Long goodsId, Long userId) {
        if (goodsId == null) {
            return getDefaultCommunityDTO(userId);
        }

        GoodsItem goodsItem = goodsItemMapper.selectById(goodsId);
        if (goodsItem == null || !StringUtils.hasText(goodsItem.getCategory())) {
            return getDefaultCommunityDTO(userId);
        }

        String goodsCategoryName = goodsItem.getCategory();
        Community community = null;

        List<GoodsCommunityCategoryMap> maps = goodsCommunityCategoryMapMapper.selectByGoodsCategoryName(goodsCategoryName);

        if (!CollectionUtils.isEmpty(maps)) {
            Optional<GoodsCommunityCategoryMap> defaultMap = maps.stream()
                    .filter(m -> m.getDefaultCommunityId() != null)
                    .findFirst();

            if (defaultMap.isPresent()) {
                community = communityMapper.selectById(defaultMap.get().getDefaultCommunityId());
            } else {
                String communityCategoryKey = maps.get(0).getCommunityCategoryKey();
                List<Community> communities = communityMapper.selectByCommunityCategoryKey(communityCategoryKey);
                if (!CollectionUtils.isEmpty(communities)) {
                    community = communities.get(0);
                }
            }
        }

        if (community == null) {
            community = getDefaultCommunity(userId);
        }

        CommunityRecommendDTO dto = new CommunityRecommendDTO();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setIcon(community.getCoverUrl());
        dto.setMemberCount(community.getMemberCount() != null ? community.getMemberCount() : 0);

        if (userId != null && community.getId() != null) {
            boolean isJoined = memberMapper.exists(community.getId(), userId) > 0;
            dto.setIsJoined(isJoined);
        } else {
            dto.setIsJoined(false);
        }

        return dto;
    }

    private Community getDefaultCommunity(Long userId) {
        Community defaultCommunity = communityMapper.selectById(301L);
        if (defaultCommunity == null) {
            defaultCommunity = new Community();
            defaultCommunity.setId(301L);
            defaultCommunity.setName("通用交流区");
            defaultCommunity.setDescription("欢迎来到通用交流区，这里有各种好物分享。");
            defaultCommunity.setCoverUrl("http://192.168.1.5:8088/static/upload/default_community_icon.png"); // 使用你最新的 IP
            defaultCommunity.setMemberCount(1000);
        }
        return defaultCommunity;
    }

    private CommunityRecommendDTO getDefaultCommunityDTO(Long userId) {
        Community defaultCommunity = getDefaultCommunity(userId);
        CommunityRecommendDTO dto = new CommunityRecommendDTO();
        dto.setId(defaultCommunity.getId());
        dto.setName(defaultCommunity.getName());
        dto.setDescription(defaultCommunity.getDescription());
        dto.setIcon(defaultCommunity.getCoverUrl());
        dto.setMemberCount(defaultCommunity.getMemberCount());
        dto.setIsJoined(false);
        return dto;
    }
}