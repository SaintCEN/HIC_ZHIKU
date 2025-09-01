package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 性能监控配置
 */
@Configuration
@Slf4j
public class PerformanceConfig implements WebMvcConfigurer {

    /**
     * 性能监控拦截器
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceInterceptor())
                .addPathPatterns("/api/**");
    }

    /**
     * 性能监控拦截器实现
     */
    public static class PerformanceInterceptor implements HandlerInterceptor {
        
        private static final String START_TIME = "startTime";
        
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            request.setAttribute(START_TIME, System.currentTimeMillis());
            return true;
        }
        
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                  Object handler, Exception ex) {
            Long startTime = (Long) request.getAttribute(START_TIME);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                String uri = request.getRequestURI();
                String method = request.getMethod();
                
                // 记录慢请求（超过1秒）
                if (duration > 1000) {
                    log.warn("慢请求检测: {} {} 耗时: {}ms", method, uri, duration);
                } else if (duration > 500) {
                    log.info("请求耗时: {} {} 耗时: {}ms", method, uri, duration);
                }
                
                // 添加响应头
                response.setHeader("X-Response-Time", duration + "ms");
            }
        }
    }
}