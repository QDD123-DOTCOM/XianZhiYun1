package com.example.xianzhiyun.config;

import com.example.xianzhiyun.utils.JwtUtil;
import com.example.xianzhiyun.utils.JsonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    // 不需要登录就能访问的接口白名单
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/admin/login",
            "/api/home",          // 【新增】放行首页数据接口
            "/api/goods",         // 【新增】放行商品列表接口
            "/images/",
            "/static/"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 1. 跨域处理 (保持不变)
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, token");

        // 2. 处理 OPTIONS 预检请求 (保持不变)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 3. 获取当前请求路径
        String path = request.getRequestURI();
        System.out.println("--- 收到请求: " + path + " ---");

        // 4. 检查白名单
        // 特殊处理：如果是获取商品详情 (/api/goods/123)，也应该放行
        boolean isGoodsDetail = path.startsWith("/api/goods/") && "GET".equalsIgnoreCase(request.getMethod());

        boolean isExcluded = isGoodsDetail || EXCLUDED_PATHS.stream().anyMatch(excluded -> {
            if (excluded.endsWith("/")) {
                return path.contains(excluded); // 匹配静态资源目录
            }
            return path.equals(excluded); // 匹配具体接口
        });

        if (isExcluded) {
            System.out.println("JwtFilter: 白名单放行 -> " + path);
            chain.doFilter(request, response);
            return;
        }

        // 5. 获取 Token (保持不变)
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        if (!StringUtils.hasText(token)) {
            token = request.getHeader("token");
        }

        // 6. 验证 Token (保持不变)
        boolean isValid = false;
        try {
            if (StringUtils.hasText(token)) {
                isValid = jwtUtil.validateToken(token);
            }
        } catch (Exception e) {
            System.out.println("Token 验证异常: " + e.getMessage());
        }

        if (isValid) {
            try {
                Long userId = jwtUtil.getClaimLong(token, "uid");
                request.setAttribute("userId", userId);
                chain.doFilter(request, response);
            } catch (Exception e) {
                send401(response, "Token 解析失败");
            }
        } else {
            System.out.println("JwtFilter: 验证失败，拦截请求！路径: " + path);
            send401(response, "身份验证过期或未登录");
        }
    }

    private void send401(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        JsonResult<String> error = JsonResult.error(401, msg);
        String json = new ObjectMapper().writeValueAsString(error);
        response.getWriter().write(json);
    }
}
