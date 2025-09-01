package com.example.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.backend.util.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全审计日志服务
 */
@Service
public class SecurityAuditService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");
    
    /**
     * 记录登录成功事件
     */
    public void logLoginSuccess(String username, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        auditLogger.info("LOGIN_SUCCESS - Username: {}, IP: {}, UserAgent: {}", 
            SecurityUtils.sanitizeForLog(username), clientIp, userAgent);
    }
    
    /**
     * 记录登录失败事件
     */
    public void logLoginFailure(String username, HttpServletRequest request, String reason) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        auditLogger.warn("LOGIN_FAILURE - Username: {}, IP: {}, UserAgent: {}, Reason: {}", 
            SecurityUtils.sanitizeForLog(username), clientIp, userAgent, reason);
    }
    
    /**
     * 记录注册事件
     */
    public void logRegistration(String username, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        auditLogger.info("REGISTRATION - Username: {}, IP: {}, UserAgent: {}", 
            SecurityUtils.sanitizeForLog(username), clientIp, userAgent);
    }
    
    /**
     * 记录密码修改事件
     */
    public void logPasswordChange(String username, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        
        auditLogger.info("PASSWORD_CHANGE - Username: {}, IP: {}", 
            SecurityUtils.sanitizeForLog(username), clientIp);
    }
    
    /**
     * 记录JWT token验证失败事件
     */
    public void logJwtValidationFailure(String token, HttpServletRequest request, String reason) {
        String clientIp = getClientIpAddress(request);
        String uri = request.getRequestURI();
        
        auditLogger.warn("JWT_VALIDATION_FAILURE - Token: {}, IP: {}, URI: {}, Reason: {}", 
            SecurityUtils.sanitizeForLog(token), clientIp, uri, reason);
    }
    
    /**
     * 记录可疑活动
     */
    public void logSuspiciousActivity(String activity, String details, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String uri = request.getRequestURI();
        
        auditLogger.error("SUSPICIOUS_ACTIVITY - Activity: {}, Details: {}, IP: {}, URI: {}, UserAgent: {}", 
            activity, details, clientIp, uri, userAgent);
    }
    
    /**
     * 记录权限访问事件
     */
    public void logAccessAttempt(String username, String resource, boolean granted, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String status = granted ? "GRANTED" : "DENIED";
        
        auditLogger.info("ACCESS_ATTEMPT - Username: {}, Resource: {}, Status: {}, IP: {}", 
            SecurityUtils.sanitizeForLog(username), resource, status, clientIp);
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}