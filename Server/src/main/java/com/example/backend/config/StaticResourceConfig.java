package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        System.out.println("=== Static Resource Configuration ===");
        System.out.println("Upload path: " + uploadPath);
        System.out.println("Resource handler: /uploads/**");
        System.out.println("Resource location: file:" + uploadPath + "/");
        
        java.io.File uploadDir = new java.io.File(uploadPath);
        System.out.println("Upload directory exists: " + uploadDir.exists());
        System.out.println("Upload directory is readable: " + uploadDir.canRead());
        System.out.println("Upload directory absolute path: " + uploadDir.getAbsolutePath());
        
        if (uploadDir.exists() && uploadDir.isDirectory()) {
            System.out.println("Upload directory contents:");
            java.io.File[] files = uploadDir.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    System.out.println("  - " + file.getName() + " (" + (file.isDirectory() ? "dir" : "file") + ")");
                }
            }
        }
        System.out.println("=====================================");
    }
}
