package com.example.xianzhiyun.entity;

import java.util.Date;

/**
 * 受助人反馈实体，对应 donation_feedback 表
 */
public class DonationFeedback {
    private Long id;
    private Long itemId;
    private String recipientName; // 可匿名
    private String message;
    private String photoUrl;
    private Date createdAt;

    public DonationFeedback() {}

    public DonationFeedback(Long id, Long itemId, String recipientName, String message, String photoUrl, Date createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.recipientName = recipientName;
        this.message = message;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DonationFeedback{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", recipientName='" + recipientName + '\'' +
                '}';
    }
}