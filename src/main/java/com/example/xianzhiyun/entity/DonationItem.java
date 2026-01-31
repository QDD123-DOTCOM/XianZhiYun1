package com.example.xianzhiyun.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * 捐赠物品实体，对应 donation_item 表
 */
public class DonationItem {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String tags; // 逗号分隔或 JSON，根据使用约定

    // 对应前端 image_url
    @JsonProperty("image_url")
    private String imageUrl;

    // 对应前端 item_condition
    @JsonProperty("item_condition")
    private String itemCondition;

    private String status;

    // donor_id（如果前端传 donor_id）
    @JsonProperty("donor_id")
    private Long donorId;

    // 前端传的是 eventId（驼峰），这里也用注解确保正确
    @JsonProperty("eventId")
    private Long eventId;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    public DonationItem() {}

    public DonationItem(Long id, String title, String description, String category, String tags, String imageUrl, String itemCondition, String status, Long donorId, Date createdAt, Date updatedAt, Long eventId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.itemCondition = itemCondition;
        this.status = status;
        this.donorId = donorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.eventId = eventId;
    }

    // getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getItemCondition() { return itemCondition; }
    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDonorId() { return donorId; }
    public void setDonorId(Long donorId) { this.donorId = donorId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "DonationItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", donorId=" + donorId +
                ", eventId=" + eventId +
                ", imageUrl=" + imageUrl +
                ", itemCondition=" + itemCondition +
                '}';
    }
}