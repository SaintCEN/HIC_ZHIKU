package com.example.backend.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 生产环境前端地址
                .allowedOriginPatterns(
                    "${app.frontend.origin}"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin")
                .exposedHeaders("X-Auth-Error", "X-Auth-Error-Detail")
                .allowCredentials(true)
                .maxAge(3600);
        
        // 为文件访问单独配置CORS
        registry.addMapping("/files/**")
                .allowedOriginPatterns(
                    "${app.frontend.origin}"
                )
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(86400);
                
        registry.addMapping("/uploads/**")
                .allowedOriginPatterns(
                    "${app.frontend.origin}"
                )
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(86400);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        // 支持多种文件访问路径
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/")
                .setCachePeriod(0); // 禁用缓存，确保文件更新立即生效
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/images/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/documents/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/documents/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/resources/")
                .setCachePeriod(3600);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


