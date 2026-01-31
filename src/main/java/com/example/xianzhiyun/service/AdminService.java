package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.AdminUser;

public interface AdminService {
    AdminUser findByUsername(String username);
    AdminUser findById(Long id);
    Long create(AdminUser user);
    void update(AdminUser user);
    void delete(Long id);

    /**
     * 验证管理员登录（示例），生产请使用更安全的认证方式
     */
    AdminUser authenticate(String username, String rawPassword);
}