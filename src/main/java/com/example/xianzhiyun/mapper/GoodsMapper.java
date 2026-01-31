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
    int insert(GoodsItem item);
    int update(GoodsItem item);

    // 关键：返回 int，表示受影响的行数
    int logicalDelete(@Param("id") Long id);

    List<GoodsItem> selectByIds(List<Long> ids);

}