package com.example.backend.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.service.SecurityAuditService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    
    @Autowired
    private SecurityAuditService securityAuditService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jws<io.jsonwebtoken.Claims> jws = jwtService.parse(token);
                Claims claims = jws.getBody();
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                if (username != null && role != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // 无效 token，清除认证上下文
                SecurityContextHolder.clearContext();
                logger.warn("JWT token validation failed for request {}: {}", request.getRequestURI(), e.getMessage());
                
                // 记录安全审计日志
                String reason = "Unknown error";
                if (e.getMessage() != null && e.getMessage().contains("expired")) {
                    reason = "Token expired";
                    logger.info("Token expired for request: {}", request.getRequestURI());
                } else if (e.getMessage() != null && e.getMessage().contains("signature")) {
                    reason = "Invalid token signature";
                    logger.warn("Invalid token signature for request: {}", request.getRequestURI());
                } else {
                    reason = "Token parsing error: " + e.getClass().getSimpleName();
                    logger.warn("Token parsing error for request: {}, error: {}", request.getRequestURI(), e.getClass().getSimpleName());
                }
                
                // 记录JWT验证失败的安全审计日志
                securityAuditService.logJwtValidationFailure(token.substring(0, Math.min(token.length(), 20)) + "...", request, reason);
                
                // 添加响应头信息，帮助前端识别token问题
                response.setHeader("X-Auth-Error", "token-error");
                response.setHeader("X-Auth-Error-Detail", e.getClass().getSimpleName());
            }
        } else {
            // 没有token或格式不正确，清除认证上下文
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}


