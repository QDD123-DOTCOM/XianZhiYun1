package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.GoodsItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {

    List<GoodsItem> selectGoodsList(Map<String, Object> params);

    int selectGoodsCount(Map<String, Object> params);

    List<GoodsItem> selectBySellerId(@Param("offset") int offset, @Param("limit") int limit, @Param("sellerId") Long sellerId);

    int countBySellerId(@Param("sellerId") Long sellerId);

    GoodsItem selectById(@Param("id") Long id);

    int insert(GoodsItem goods);

    int update(GoodsItem goods);

    int logicalDelete(@Param("id") Long id);

    List<GoodsItem> selectByIds(@Param("list") List<Long> ids);

    // --- 新增统计字段更新方法 ---
    void incrementViewCount(@Param("id") Long id);

    void updateFavCount(@Param("id") Long id, @Param("delta") int delta);

    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);

    void incrementChatCount(@Param("id") Long id);
}