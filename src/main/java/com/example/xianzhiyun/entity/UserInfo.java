package com.example.xianzhiyun.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String email;
    private String role;       // 已有
    private String password;   // 新增：存储 bcrypt 哈希
    private Integer creditScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}