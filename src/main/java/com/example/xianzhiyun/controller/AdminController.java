package com.example.xianzhiyun.controller;

import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.AdminUser;
import com.example.xianzhiyun.service.AdminService;
import com.example.xianzhiyun.utils.JsonResult;
import com.example.xianzhiyun.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public JsonResult<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return JsonResult.error(400, "参数错误");
        }

        AdminUser u = adminService.authenticate(username, password);
        if (u == null) {
            return JsonResult.error(401, "用户名或密码错误");
        }

        // 生成 token
        Map<String, Object> claims = new HashMap<>();
        // 【重要修复】Key 改为 "uid"，与 JwtFilter 保持一致
        claims.put("uid", u.getId());
        claims.put("username", u.getUsername());
        claims.put("role", u.getRole());

        String token = jwtUtil.generateToken(claims, u.getUsername());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);

        Map<String, Object> admin = new HashMap<>();
        admin.put("id", u.getId());
        admin.put("username", u.getUsername());
        admin.put("role", u.getRole());
        resp.put("admin", admin);

        return JsonResult.success(resp);
    }
}