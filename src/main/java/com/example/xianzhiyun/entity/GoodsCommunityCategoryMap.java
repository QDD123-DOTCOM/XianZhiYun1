package com.example.xianzhiyun.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GoodsCommunityCategoryMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String goodsCategoryName; // 商品分类名称，对应 goods_item.category
    private String communityCategoryKey; // 社区分类Key，对应 community.category_key
    private Long defaultCommunityId; // 默认推荐社区ID (可选)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsCategoryName() {
        return goodsCategoryName;
    }

    public void setGoodsCategoryName(String goodsCategoryName) {
        this.goodsCategoryName = goodsCategoryName;
    }

    public String getCommunityCategoryKey() {
        return communityCategoryKey;
    }

    public void setCommunityCategoryKey(String communityCategoryKey) {
        this.communityCategoryKey = communityCategoryKey;
    }

    public Long getDefaultCommunityId() {
        return defaultCommunityId;
    }

    public void setDefaultCommunityId(Long defaultCommunityId) {
        this.defaultCommunityId = defaultCommunityId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "GoodsCommunityCategoryMap{" +
                "id=" + id +
                ", goodsCategoryName='" + goodsCategoryName + '\'' +
                ", communityCategoryKey='" + communityCategoryKey + '\'' +
                ", defaultCommunityId=" + defaultCommunityId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}