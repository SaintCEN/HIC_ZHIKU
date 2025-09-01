package com.example.backend.config;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * API请求频率限制配置
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register"); // 登录注册有单独的限制
    }

    /**
     * 请求频率限制拦截器
     */
    public static class RateLimitInterceptor implements HandlerInterceptor {
        
        // 存储每个IP的请求计数和时间窗口
        private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
        
        // 通用API限制：每分钟100次请求
        private static final int GENERAL_LIMIT = 100;
        private static final Duration GENERAL_WINDOW = Duration.ofMinutes(1);
        
        // 认证API限制：每分钟5次请求
        private static final int AUTH_LIMIT = 5;
        private static final Duration AUTH_WINDOW = Duration.ofMinutes(1);

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String clientIp = getClientIpAddress(request);
            String uri = request.getRequestURI();
            
            // 确定限制参数
            int limit = GENERAL_LIMIT;
            Duration window = GENERAL_WINDOW;
            
            if (uri.startsWith("/api/auth/")) {
                limit = AUTH_LIMIT;
                window = AUTH_WINDOW;
            }
            
            // 检查请求频率
            if (!isAllowed(clientIp + ":" + getCategory(uri), limit, window)) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"code\": 429, \"message\": \"请求过于频繁，请稍后再试\"}");
                return false;
            }
            
            return true;
        }
        
        private boolean isAllowed(String key, int limit, Duration window) {
            long now = System.currentTimeMillis();
            RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter());
            
            synchronized (counter) {
                // 如果时间窗口已过期，重置计数器
                if (now - counter.windowStart > window.toMillis()) {
                    counter.count.set(0);
                    counter.windowStart = now;
                }
                
                // 检查是否超过限制
                if (counter.count.get() >= limit) {
                    return false;
                }
                
                // 增加计数
                counter.count.incrementAndGet();
                return true;
            }
        }
        
        private String getCategory(String uri) {
            if (uri.startsWith("/api/auth/")) {
                return "auth";
            }
            return "general";
        }
        
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
        
        /**
         * 请求计数器
         */
        private static class RequestCounter {
            private final AtomicInteger count = new AtomicInteger(0);
            private volatile long windowStart = System.currentTimeMillis();
        }
    }
}