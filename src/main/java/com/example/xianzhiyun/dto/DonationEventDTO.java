package com.example.xianzhiyun.dto;

import java.util.Date;

/**
 * DonationEvent DTO
 */
public class DonationEventDTO {
    private Long id;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String location;
    private String imageUrl;
    private Date createTime;
    private Date updateTime;

    public DonationEventDTO() {}

    public DonationEventDTO(Long id, String title, String description, Date startTime, Date endTime, String location, String imageUrl, Date createTime, Date updateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.imageUrl = imageUrl;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DonationEventDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}