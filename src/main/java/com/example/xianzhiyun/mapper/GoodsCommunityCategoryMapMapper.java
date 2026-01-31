package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.GoodsCommunityCategoryMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsCommunityCategoryMapMapper {

    /**
     * 根据商品分类名称查询映射列表
     * @param goodsCategoryName 商品分类名称
     * @return 映射列表，优先返回有 defaultCommunityId 的
     */
    List<GoodsCommunityCategoryMap> selectByGoodsCategoryName(@Param("goodsCategoryName") String goodsCategoryName);

    /**
     * 根据ID查询映射
     * @param id
     * @return
     */
    GoodsCommunityCategoryMap selectById(@Param("id") Long id);
}