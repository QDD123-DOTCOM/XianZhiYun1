package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.GoodsItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsItemMapper {

    /** 首页推荐：最新上架且状态为 ON_SALE */
    List<GoodsItem> selectRecommend(@Param("limit") int limit);

    /** 根据 ID 查询商品详情 */
    GoodsItem selectById(@Param("id") Long id);

    /** 分页查询某用户发布的商品 */
    List<GoodsItem> selectBySellerId(@Param("sellerId") Long sellerId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    /** 统计某用户发布的商品数量 */
    int countBySellerId(@Param("sellerId") Long sellerId);

    /** 新增商品（插入后自动回填 id） */
    int insert(GoodsItem goods);

    /** 更新商品 */
    int update(GoodsItem goods);

    /** 逻辑删除（将 status 改为 REMOVED） */
    int logicalDelete(@Param("id") Long id);

    /** 物理删除（若需要） */
    int deleteById(@Param("id") Long id);
    List<GoodsItem> selectAdminList(@Param("params") Map<String, Object> params);
    int selectAdminCount(@Param("params") Map<String, Object> params);

}