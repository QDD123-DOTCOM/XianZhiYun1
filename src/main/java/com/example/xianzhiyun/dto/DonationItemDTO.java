package com.example.xianzhiyun.dto;

import java.util.Date;

/**
 * DonationItem DTO，用于前端请求/响应
 */
public class DonationItemDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String tags; // 逗号分隔字符串或 JSON 字符串
    private String imageUrl;
    private String itemCondition;
    private String status;
    private Long donorId;
    private Date createdAt;
    private Date updatedAt;
    private Long eventId; // 可选：关联活动 id（如果 donation_item 表有此字段）

    public DonationItemDTO() {}

    public DonationItemDTO(Long id, String title, String description, String category, String tags, String imageUrl, String itemCondition, String status, Long donorId, Date createdAt, Date updatedAt, Long eventId) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "DonationItemDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", donorId=" + donorId +
                '}';
    }
}