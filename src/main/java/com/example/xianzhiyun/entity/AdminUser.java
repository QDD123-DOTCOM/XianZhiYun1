package com.example.xianzhiyun.entity;

import java.util.Date;

/**
 * 管理员账号实体，对应 admin_user 表
 */
public class AdminUser {
    private Long id;
    private String username;
    private String password; // 存储加密后的密码（bcrypt 等）
    private String role; // SUPER_ADMIN / AUDITOR 等
    private Date createdAt;
    private Date updatedAt;

    public AdminUser() {}

    public AdminUser(Long id, String username, String password, String role, Date createdAt, Date updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * 注意：传入的 password 应为已加密字符串
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}