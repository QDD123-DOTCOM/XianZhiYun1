package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.CommunityCategory;

import java.util.List;

public interface CommunityCategoryService {
    // 保留这个方法
    List<CommunityCategory> listActiveCategories();

    CommunityCategory getByKey(String categoryKey);
    CommunityCategory getById(Long id);
    int createCategory(CommunityCategory category);
    int updateCategory(CommunityCategory category);
    int deleteCategory(Long id);

    // 【删除】下面这行代码，因为它和第一行重复了，且实现类没写它
    // List<CommunityCategory> findAllActive();
}