package com.example.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url}")
    private String uploadUrl;
    
    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "images", new String[]{"jpg", "jpeg", "png", "gif", "webp"});
    }
    
    /**
     * 上传文档
     */
    @PostMapping("/upload/document")
    public ResponseEntity<Map<String, Object>> uploadDocument(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "documents", new String[]{"pdf", "doc", "docx", "txt", "md"});
    }
    
    /**
     * 上传资源文件
     */
    @PostMapping("/upload/resource")
    public ResponseEntity<Map<String, Object>> uploadResource(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "resources", new String[]{"zip", "rar", "7z", "tar", "gz"});
    }
    
    /**
     * 通用文件上传方法
     */
    private ResponseEntity<Map<String, Object>> uploadFile(MultipartFile file, String subDir, String[] allowedExtensions) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                response.put("success", false);
                response.put("message", "文件名无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            String extension = getFileExtension(originalFilename);
            
            // 检查文件扩展名
            if (!isAllowedExtension(extension, allowedExtensions)) {
                response.put("success", false);
                response.put("message", "不支持的文件类型");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + "." + extension;
            
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, subDir);
            Files.createDirectories(uploadDir);
            
            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 构建文件URL
            String fileUrl = uploadUrl + "/" + subDir + "/" + filename;
            
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("filename", filename);
            response.put("originalFilename", originalFilename);
            response.put("url", fileUrl);
            response.put("size", file.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * 检查文件扩展名是否允许
     */
    private boolean isAllowedExtension(String extension, String[] allowedExtensions) {
        for (String allowed : allowedExtensions) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
