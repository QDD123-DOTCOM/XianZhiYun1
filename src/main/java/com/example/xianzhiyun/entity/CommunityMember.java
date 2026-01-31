package com.example.xianzhiyun.entity;

import java.sql.Timestamp;

public class CommunityMember {
    private Long communityId;
    private Long userId;
    private String role;
    private Timestamp joinTime;

    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getJoinTime() { return joinTime; }
    public void setJoinTime(Timestamp joinTime) { this.joinTime = joinTime; }
}