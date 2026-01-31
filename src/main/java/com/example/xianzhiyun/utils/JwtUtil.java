package com.example.xianzhiyun.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（基于 jjwt）。
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expirationSeconds:86400}") long expirationSeconds) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationSeconds * 1000L;
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256);
        return builder.compact();
    }

    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getClaimString(String token, String claimKey) {
        if (token == null || claimKey == null) return null;
        try {
            Claims claims = parseToken(token).getBody();
            Object v = claims.get(claimKey);
            return v != null ? String.valueOf(v) : null;
        } catch (JwtException ex) {
            return null;
        }
    }

    public Long getClaimLong(String token, String claimKey) {
        String s = getClaimString(token, claimKey);
        if (s == null) return null;
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String resolveTokenFromRequest(HttpServletRequest request) {
        if (request == null) return null;
        String header = request.getHeader("Authorization");
        if (header == null) header = request.getHeader("authorization");
        if (header == null) return null;
        header = header.trim();
        if (header.length() == 0) return null;
        if (header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return header;
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String token = resolveTokenFromRequest(request);
            if (token == null) return null;
            return getClaimLong(token, "uid");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (token == null) return false;
        try {
            parseToken(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    // 【新增方法】为了兼容你 Controller 中可能的直接调用需求
    // 假设你的 Token 中存储用户 ID 的 key 是 "uid"
    public Long getUserId(String token) {
        return getClaimLong(token, "uid");
    }
}