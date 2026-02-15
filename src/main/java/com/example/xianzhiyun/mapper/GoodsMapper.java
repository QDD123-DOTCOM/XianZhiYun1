package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.GoodsItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {

    /**
     * 【新增】首页推荐：获取最新上架的商品
     * 对应 XML 中的 id="selectRecommend"
     */
    List<GoodsItem> selectRecommend(@Param("limit") int limit);

    /**
     * 分页查询商品列表（带筛选条件）
     */
    List<GoodsItem> selectGoodsList(Map<String, Object> params);

    /**
     * 统计商品数量（带筛选条件）
     */
    int selectGoodsCount(Map<String, Object> params);

    /**
     * 分页查询某用户发布的商品
     */
    List<GoodsItem> selectBySellerId(@Param("offset") int offset, @Param("limit") int limit, @Param("sellerId") Long sellerId);

    /**
     * 统计某用户发布的商品数量
     */
    int countBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 根据 ID 查询商品详情
     */
    GoodsItem selectById(@Param("id") Long id);

    /**
     * 新增商品
     */
    int insert(GoodsItem goods);

    /**
     * 更新商品
     */
    int update(GoodsItem goods);

    /**
     * 逻辑删除
     */
    int logicalDelete(@Param("id") Long id);

    /**
     * 根据 ID 列表批量查询
     */
    List<GoodsItem> selectByIds(@Param("list") List<Long> ids);

    // --- 统计字段更新方法 ---

    /**
     * 增加浏览量
     */
    void incrementViewCount(@Param("id") Long id);

    /**
     * 更新收藏数 (delta 为 1 或 -1)
     */
    void updateFavCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 更新评论数 (delta 为 1 或 -1)
     */
    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 增加咨询数
     */
    void incrementChatCount(@Param("id") Long id);
}