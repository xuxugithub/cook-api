package com.cooking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:cooking-app-secret-key-2024}")
    private String secretKey;

    @Value("${jwt.expire-time:604800000}")
    private long expireTime;

    private static SecretKey SECRET_KEY;
    private static long EXPIRE_TIME;

    @PostConstruct
    public void init() {
        // 确保密钥长度足够（至少32字节用于HS256）
        String key = this.secretKey;
        if (key.length() < 32) {
            key = key + "0123456789abcdef0123456789abcdef".substring(key.length());
        }
        SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        EXPIRE_TIME = this.expireTime;
    }

    /**
     * 生成JWT token（实例方法）
     *
     * @param claims 载荷数据
     * @return token
     */
    public static String createJWT(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT token（静态方法）
     *
     * @param claims 载荷数据
     * @return token
     */
    public static String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中获取载荷
     *
     * @param token token
     * @return 载荷
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return Long.valueOf(userId.toString());
    }

    /**
     * 从token中获取管理员ID
     *
     * @param token token
     * @return 管理员ID
     */
    public static Long getAdminIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object adminId = claims.get("adminId");
        if (adminId instanceof Integer) {
            return ((Integer) adminId).longValue();
        }
        return Long.valueOf(adminId.toString());
    }

    /**
     * 判断token类型是否为管理员
     *
     * @param token token
     * @return 是否为管理员token
     */
    public static boolean isAdminToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "admin".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证token是否有效
     *
     * @param token token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
