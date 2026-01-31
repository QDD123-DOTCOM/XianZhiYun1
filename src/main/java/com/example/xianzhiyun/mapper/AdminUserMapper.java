package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.xianzhiyun.entity.AdminUser;

import java.util.List;

@Mapper
public interface AdminUserMapper {
    AdminUser selectById(@Param("id") Long id);
    AdminUser selectByUsername(@Param("username") String username);
    int insert(AdminUser user);
    int update(AdminUser user);
    int deleteById(@Param("id") Long id);
    List<AdminUser> selectAll();
}