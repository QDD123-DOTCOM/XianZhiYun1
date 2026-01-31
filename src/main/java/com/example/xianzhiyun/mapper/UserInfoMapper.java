package com.example.xianzhiyun.mapper;

import com.example.xianzhiyun.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select; // 必须导入这个

import java.util.List;

@Mapper
public interface UserInfoMapper {

    // 假设这些方法在 XML 中有定义，或者你可以根据需要添加注解
    List<UserInfo> findAll();

    UserInfo findById(Long id);

    UserInfo findByOpenid(@Param("openid") String openid);

    int insert(UserInfo user);

    int update(UserInfo user);

    int deleteById(Long id);

    /**
     * 查询信用分（可能为 null）
     */
    Integer selectCreditScoreById(@Param("id") Long id);

    /**
     * 按 delta 增减用户信用分，并将结果限定在 0..10
     * 返回影响行数
     */
    int updateCreditScoreByDelta(@Param("userId") Long userId, @Param("delta") Integer delta);

    // 新增：读取用户信用分
    Integer selectCreditById(Long userId);

    // 新增：更新用户信用分为指定值
    int updateCredit(Long userId, Integer newCredit);

    int updateSelective(UserInfo user);

    List<UserInfo> selectAll();

    List<UserInfo> selectByOpenidOrNickname(@Param("kw") String kw);

    // =======================================================
    // 关键修复：给 selectById 添加注解，确保 CommunityService 能调用到 SQL
    // =======================================================
    @Select("SELECT * FROM user_info WHERE id = #{id}")
    UserInfo selectById(@Param("id") Long id);
}