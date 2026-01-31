package com.example.xianzhiyun.dto;

import java.util.Date;

public class ArticleDTO {
    private Long id;
    private Long communityId; // 新增：社区ID
    private String title;
    private String coverUrl;
    private String images;    // 新增：图片列表的JSON字符串
    private String summary;   // 新增：文章摘要
    private String content;

    // 统计数据
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Date createTime;

    // 作者信息 (用于详情页帖子列表)
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    // 额外状态 (例如用户是否点赞了当前帖子)
    private Boolean liked; // 新增：当前用户是否点赞

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCommunityId() { return communityId; } // 新增 getter
    public void setCommunityId(Long communityId) { this.communityId = communityId; } // 新增 setter

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getImages() { return images; } // 新增 getter
    public void setImages(String images) { this.images = images; } // 新增 setter

    public String getSummary() { return summary; } // 新增 getter
    public void setSummary(String summary) { this.summary = summary; } // 新增 setter

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public Boolean getLiked() { return liked; } // 新增 getter
    public void setLiked(Boolean liked) { this.liked = liked; } // 新增 setter
}