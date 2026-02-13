package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.GoodsItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {
    // ... 原有方法保持不变 ...
    List<GoodsItem> selectGoodsList(Map<String, Object> params);
    int selectGoodsCount(Map<String, Object> params);
    List<GoodsItem> selectBySellerId(@Param("offset") int offset, @Param("limit") int limit, @Param("sellerId") Long sellerId);
    int countBySellerId(@Param("sellerId") Long sellerId);
    GoodsItem selectById(@Param("id") Long id);
    int insert(GoodsItem goods);
    int update(GoodsItem goods);
    int logicalDelete(@Param("id") Long id);
    List<GoodsItem> selectByIds(@Param("list") List<Long> ids);

    // 【新增统计方法】对应 XML 中的 update 标签
    void incrementViewCount(@Param("id") Long id);
    void updateFavCount(@Param("id") Long id, @Param("delta") int delta);
    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);
    void incrementChatCount(@Param("id") Long id);
}