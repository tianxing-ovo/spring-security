package com.ltx.util;


import com.ltx.constant.Constant;
import com.ltx.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Jwt工具类
 *
 * @author tianxing
 */
public class JwtUtil {

    private static final String SECRET = "/r3cvNod5rgpBq69NuSX1eseTdx4xiJQYRTJcGKovlE=";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成密钥
     */
    public static String genSecret() {
        // 生成适合HMAC-SHA-256算法的密钥
        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 创建JWS
     *
     * @param user        用户
     * @param authorities 权限列表
     * @param expireTime  过期时间
     * @return JWS
     */
    public static String createJws(User user, List<String> authorities, long expireTime) {
        // 发行时间
        Date issueDate = new Date();
        // 过期时间
        Date expireDate = new Date(issueDate.getTime() + expireTime);
        // 自定义声明
        Map<String, Object> claimMap = new HashMap<>();
        // 用户信息
        claimMap.put(Constant.USER, user);
        // 权限列表
        claimMap.put(Constant.AUTHORITIES, authorities);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(issueDate)
                .expiration(expireDate)
                .claims(claimMap)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWS
     *
     * @param jws JWS
     * @return JWS
     */
    public static Jws<Claims> parseJws(String jws) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(jws);
    }

    /**
     * 验证JWS
     *
     * @param jws JWS
     * @return 是否有效
     */
    public static boolean verifyJws(String jws) {
        try {
            parseJws(jws);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取载荷
     *
     * @param jws JWS
     * @return 载荷
     */
    public static Claims getPayLoad(String jws) {
        return parseJws(jws).getPayload();
    }
}
