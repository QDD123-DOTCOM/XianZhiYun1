package com.example.xianzhiyun.entity;

import java.util.Date;

/**
 * 审核记录实体，对应 donation_review 表
 */
public class DonationReview {
    private Long id;
    private Long itemId;
    private Long reviewerId; // 可为 null
    private String reviewStatus; // APPROVED / REJECTED 等
    private String reviewComment;
    private Date reviewTime;

    public DonationReview() {}

    public DonationReview(Long id, Long itemId, Long reviewerId, String reviewStatus, String reviewComment, Date reviewTime) {
        this.id = id;
        this.itemId = itemId;
        this.reviewerId = reviewerId;
        this.reviewStatus = reviewStatus;
        this.reviewComment = reviewComment;
        this.reviewTime = reviewTime;
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

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    @Override
    public String toString() {
        return "DonationReview{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", reviewStatus='" + reviewStatus + '\'' +
                '}';
    }
}