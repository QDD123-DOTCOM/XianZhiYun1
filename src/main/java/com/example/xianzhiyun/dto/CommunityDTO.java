package com.example.xianzhiyun.dto;

import java.util.List;

public class CommunityDTO {
    private Long id;
    private String name;
    private String categoryKey;
    private String description;
    private String coverUrl;
    private Integer memberCount;
    private Integer topicCount;
    private List<ArticleDTO> latestArticles;

    // 【新增字段】：用于存储当前用户是否已加入该社区的状态
    private boolean isJoined;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }

    public Integer getTopicCount() { return topicCount; }
    public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }

    public List<ArticleDTO> getLatestArticles() { return latestArticles; }
    public void setLatestArticles(List<ArticleDTO> latestArticles) { this.latestArticles = latestArticles; }

    // 【修正 Getter/Setter】：确保逻辑正确
    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }
}