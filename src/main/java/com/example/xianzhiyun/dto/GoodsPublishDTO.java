package com.example.xianzhiyun.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal; // 1. 必须导入 BigDecimal
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // 2. 建议增加 NotNull 校验

/**
 * 商品发布和更新请求参数 DTO
 * 用于接收前端的 title, description, price, category, coverUrls
 */
@Data
public class GoodsPublishDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    /**
     * 核心修改：将 Double 更改为 BigDecimal
     * 解决 'setPrice(java.math.BigDecimal)' 无法应用于 Double 的问题
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotBlank(message = "分类不能为空")
    private String category;

    /**
     * 核心：接收小程序上传的图片URL数组。
     */
    private List<String> coverUrls;

    /**
     * 新增字段：兼容前端可能只上传一张图片时，直接发送一个字符串的情况。
     */
    private String coverUrl;

    /**
     * 可选：商品二级类型（周边类型）
     */
    private String itemType;
}