package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.xianzhiyun.mapper.CommunityCategoryMapper;
import com.example.xianzhiyun.entity.CommunityCategory;
import com.example.xianzhiyun.service.CommunityCategoryService;

import java.util.List;

@Service
public class CommunityCategoryServiceImpl implements CommunityCategoryService {

    @Autowired
    private CommunityCategoryMapper categoryMapper;

    /**
     * 修正点：这里的方法名必须是 listActiveCategories
     * 因为报错提示接口定义的是 listActiveCategories()
     */
    @Override
    public List<CommunityCategory> listActiveCategories() {
        // 这里调用 Mapper 的方法保持不变 (通常 Mapper 叫 selectAllActive)
        return categoryMapper.selectAllActive();
    }

    @Override
    public CommunityCategory getByKey(String categoryKey) {
        return categoryMapper.selectByKey(categoryKey);
    }

    @Override
    public CommunityCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public int createCategory(CommunityCategory category) {
        return categoryMapper.insert(category);
    }

    @Override
    public int updateCategory(CommunityCategory category) {
        return categoryMapper.update(category);
    }

    @Override
    public int deleteCategory(Long id) {
        return categoryMapper.deleteById(id);
    }
}