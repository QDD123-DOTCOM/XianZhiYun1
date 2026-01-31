package com.example.xianzhiyun.dto;

import java.util.List;
import java.util.Map;

public class CommunityDetailDTO extends CommunityDTO {
    // 注意：这里最好继承 CommunityDTO，这样能复用基础字段。
    // 如果你不想继承，需要把 CommunityDTO 的字段（id, name, description...）也复制过来。
    // 下面假设你已经继承了 CommunityDTO，或者手动补全了基础字段。
    // 为了防止报错，我这里把基础字段也列出来（如果继承了请删除重复字段）。

    /* 如果继承了 CommunityDTO，请注释掉以下重复字段 */
    /*
    private Long id;
    private String name;
    private String categoryKey;
    private String description;
    private String coverUrl;
    private Integer memberCount;
    private Integer topicCount;
    */

    // 详情页特有字段
    private Boolean isJoined; // 是否已加入
    private String myRole;    // 当前用户角色 (GUEST, MEMBER, ADMIN...)
    private List<Map<String, Object>> sampleMembers; // 成员头像预览

    // Getters and Setters

    // 兼容 Service 层调用的 setJoined
    public void setJoined(Boolean joined) {
        this.isJoined = joined;
    }

    public Boolean getJoined() {
        return isJoined;
    }

    // 原有的 setIsJoined 保留
    public void setIsJoined(Boolean isJoined) {
        this.isJoined = isJoined;
    }

    public Boolean getIsJoined() {
        return isJoined;
    }

    public String getMyRole() {
        return myRole;
    }

    public void setMyRole(String myRole) {
        this.myRole = myRole;
    }

    public List<Map<String, Object>> getSampleMembers() {
        return sampleMembers;
    }

    public void setSampleMembers(List<Map<String, Object>> sampleMembers) {
        this.sampleMembers = sampleMembers;
    }
}