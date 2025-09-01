package com.example.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtRefreshService {
    private final SecretKey refreshKey;
    private final long refreshExpireMinutes;
    
    // 用于存储已撤销的token（生产环境应使用Redis等外部存储）
    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public JwtRefreshService(@Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.refresh-expire-minutes:10080}") long refreshExpireMinutes) { // 默认7天
        // 为refresh token使用不同的密钥（在实际生产中应该使用完全不同的密钥）
        String refreshSecret = secret + "_refresh";
        byte[] bytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(refreshSecret.getBytes()));
        this.refreshKey = Keys.hmacShaKeyFor(bytes);
        this.refreshExpireMinutes = refreshExpireMinutes;
    }

    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("type", "refresh");
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpireMinutes, ChronoUnit.MINUTES)))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            if (isTokenRevoked(token)) {
                return false;
            }
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            Claims claims = jws.getBody();
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromRefreshToken(String token) {
        try {
            if (isTokenRevoked(token)) {
                return null;
            }
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            Claims claims = jws.getBody();
            Object uid = claims.get("uid");
            if (uid instanceof Integer) {
                return ((Integer) uid).longValue();
            } else if (uid instanceof Long) {
                return (Long) uid;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsernameFromRefreshToken(String token) {
        try {
            if (isTokenRevoked(token)) {
                return null;
            }
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return jws.getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public void revokeToken(String token) {
        revokedTokens.put(token, Instant.now());
        // 清理过期的撤销记录
        cleanupRevokedTokens();
    }

    private boolean isTokenRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    private void cleanupRevokedTokens() {
        Instant cutoff = Instant.now().minus(refreshExpireMinutes + 60, ChronoUnit.MINUTES);
        revokedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }
}