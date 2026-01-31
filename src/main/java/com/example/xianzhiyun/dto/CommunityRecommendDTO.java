package com.example.xianzhiyun.dto;

import java.io.Serializable;

public class CommunityRecommendDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String icon; // 对应 community.cover_url
    private Integer memberCount;
    private Boolean isJoined; // 当前用户是否已加入该社区

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Boolean getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(Boolean joined) {
        isJoined = joined;
    }

    @Override
    public String toString() {
        return "CommunityRecommendDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", memberCount=" + memberCount +
                ", isJoined=" + isJoined +
                '}';
    }
}