package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.service.UserInfoService;
import com.example.xianzhiyun.utils.JsonResult;
import com.example.xianzhiyun.utils.JwtUtil; // 【新增导入】
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // 【新增导入】
import java.util.Map;     // 【新增导入】

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserInfoService userInfoService;
    private final JwtUtil jwtUtil; // 【修改点 1】注入 JwtUtil
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 【修改点 2】构造函数加入 JwtUtil
    public AuthController(UserInfoService userInfoService, JwtUtil jwtUtil) {
        this.userInfoService = userInfoService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/register")
    public JsonResult<RegisterResp> register(@RequestBody RegisterReq req) {
        if (req == null || req.getAccount() == null || req.getAccount().trim().isEmpty()
                || req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            return JsonResult.error(400, "account 和 password 必填");
        }
        String account = req.getAccount().trim();
        String role = (req.getRole() == null || req.getRole().trim().isEmpty()) ? "STUDENT" : req.getRole().trim().toUpperCase();
        UserInfo exist = userInfoService.getByOpenid(account);
        if (exist != null) {
            return JsonResult.error(409, "用户已存在");
        }
        UserInfo u = new UserInfo();
        u.setOpenid(account);
        u.setNickname(req.getNickname() != null ? req.getNickname() : ((role.equals("TEACHER") ? "老师_" : "学生_") + account));
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setRole(role);
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        userInfoService.create(u);
        UserInfo created = userInfoService.getByOpenid(account);
        if (created == null) {
            return JsonResult.error(500, "创建失败");
        }
        RegisterResp r = new RegisterResp();
        r.setUserId(created.getId());
        return JsonResult.success(r);
    }

    @PostMapping("/login")
    public JsonResult<LoginResp> login(@RequestBody LoginReq req) {
        if (req == null || req.getAccount() == null || req.getAccount().trim().isEmpty()
                || req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            return JsonResult.error(400, "account 和 password 必填");
        }
        String account = req.getAccount().trim();
        UserInfo exist = userInfoService.getByOpenid(account);
        if (exist == null) {
            return JsonResult.error(401, "账号或密码错误");
        }
        String dbPass = exist.getPassword();
        if (dbPass == null || !passwordEncoder.matches(req.getPassword(), dbPass)) {
            return JsonResult.error(401, "账号或密码错误");
        }
        LoginResp resp = new LoginResp();
        resp.setUserId(exist.getId());
        resp.setRole(exist.getRole());
        resp.setNickname(exist.getNickname());
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", exist.getId());
        claims.put("role", exist.getRole());
        String token = jwtUtil.generateToken(claims, exist.getOpenid());
        resp.setToken(token);
        return JsonResult.success(resp);
    }
    public static class LoginReq {
        private String account;
        private String password;
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    public static class LoginResp {
        private Long userId;
        private String role;
        private String nickname;
        private String token;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
    public static class RegisterReq {
        private String role;
        private String account;
        private String nickname;
        private String phone;
        private String email;
        private String password;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getAccount() { return account; }
        public void setAccount(String account) { this.account = account; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    public static class RegisterResp {
        private Long userId;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
}