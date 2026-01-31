package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.UserInfo;

import java.util.List;

public interface UserInfoService {
    List<UserInfo> listAll();
    UserInfo getById(Long id);
    UserInfo create(UserInfo user);
    UserInfo update(UserInfo user);
    void delete(Long id);

    // 个人中心
    UserInfo getProfile(Long id);
    void updateProfile(UserInfo user);

    int getPoints(Long id);
    int getFavoritesCount(Long id);
    int getActivitiesCount(Long id);

    // 新增：删除本地上传文件（如果属于 upload.dir）
    void deleteLocalUploadFileIfExists(String fileUrl);

    // 新增：按 openid 查询用户（用于登录/注册）
    UserInfo getByOpenid(String openid);
    List<UserInfo> findByKeyword(String keyword);
}