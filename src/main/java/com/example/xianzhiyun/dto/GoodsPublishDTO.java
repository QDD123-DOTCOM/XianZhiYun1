package com.example.xianzhiyun.dto;

import lombok.Data;
import java.util.List;
// 核心修改：将 javax 替换为 jakarta
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotEmpty; // <-- 移除此行

/**
 * 商品发布和更新请求参数 DTO
 * 用于接收前端的 title, description, price, category, coverUrls
 */
@Data
public class GoodsPublishDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    // 价格必须大于等于 0.01
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private Double price;

    @NotBlank(message = "分类不能为空")
    private String category;

    /**
     * 核心：接收小程序上传的图片URL数组。
     * 注意：移除了 @NotEmpty 注解，校验逻辑转移到 Service 层。
     */
    private List<String> coverUrls;

    /**
     * 新增字段：兼容前端可能只上传一张图片时，直接发送一个字符串的情况。
     */
    private String coverUrl;

    /**
     * 可选：商品二级类型（周边类型），例如 official / limited / collectible / practical / diy / digital / ticket / device
     */
    private String itemType;
}