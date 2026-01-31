package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.xianzhiyun.entity.AdminUser;
import com.example.xianzhiyun.mapper.AdminUserMapper;
import com.example.xianzhiyun.service.AdminService;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser findByUsername(String username) {
        return adminUserMapper.selectByUsername(username);
    }

    @Override
    public AdminUser findById(Long id) {
        return adminUserMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(AdminUser user) {
        adminUserMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional
    public void update(AdminUser user) {
        adminUserMapper.update(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        adminUserMapper.deleteById(id);
    }

    @Override
    public AdminUser authenticate(String username, String rawPassword) {
        AdminUser u = adminUserMapper.selectByUsername(username);
        if (u == null) return null;
        // 简单示例：如果你保存的是 bcrypt，则此处需用 BCrypt.checkpw
        if (Objects.equals(u.getPassword(), rawPassword)) {
            return u;
        }
        return null;
    }
}