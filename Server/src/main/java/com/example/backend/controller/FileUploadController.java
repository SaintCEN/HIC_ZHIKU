package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.backend.mapper.UserMapper;
import com.example.backend.entity.User;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadPath;

    @org.springframework.beans.factory.annotation.Autowired
    private UserMapper userMapper;

    @PostMapping("/avatar")
    @Transactional
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件不能为空"));
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("message", "只能上传图片文件"));
            }

            // 验证文件大小 (2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件大小不能超过2MB"));
            }

            // 获取用户ID
            String username = authentication.getName();
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "用户认证信息无效"));
            }
            
            // 创建上传目录
            String avatarDir = uploadPath + "/users/avatars";
            File dir = new File(avatarDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    // 检查是否因为权限问题导致创建失败
                    String errorMsg = "无法创建上传目录: " + avatarDir;
                    if (!dir.getParentFile().exists()) {
                        errorMsg += " (父目录不存在)";
                    } else if (!dir.getParentFile().canWrite()) {
                        errorMsg += " (父目录无写权限)";
                    }
                    return ResponseEntity.internalServerError().body(Map.of("message", errorMsg));
                }
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = username + "_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            // 保存文件
            Path filePath = Paths.get(avatarDir, filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 验证文件是否真的被保存了
            if (!Files.exists(filePath)) {
                return ResponseEntity.internalServerError().body(Map.of("message", "文件保存失败"));
            }

            // 返回文件URL
            String fileUrl = "/uploads/users/avatars/" + filename;
            
            // 持久化到用户表的 avatar_url 字段，并记录旧头像以便删除
            User existing = userMapper.findByUsername(username);
            if (existing == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "用户不存在: " + username));
            }
            
            String oldUrl = existing.getAvatarUrl();
            
            try {
                // 使用原来验证过的updateProfile方法
                User updateUser = new User();
                updateUser.setId(existing.getId());
                updateUser.setAvatarUrl(fileUrl);
                int updateResult = userMapper.updateProfile(updateUser);
                if (updateResult == 0) {
                    return ResponseEntity.internalServerError().body(Map.of("message", "数据库更新失败"));
                }
            } catch (Exception dbError) {
                return ResponseEntity.internalServerError().body(Map.of("message", "数据库错误: " + dbError.getMessage()));
            }

            // 删除旧头像文件（仅限 uploads/users/avatars/ 下的文件）
            if (oldUrl != null && !oldUrl.isBlank() && !oldUrl.equals(fileUrl)) {
                try {
                    String rel = null;
                    int idx = oldUrl.indexOf("/uploads/");
                    if (idx >= 0) {
                        rel = oldUrl.substring(idx + "/uploads/".length()); // e.g. users/avatars/xxx.png
                    } else if (oldUrl.startsWith("/")) {
                        // 处理形如 "/uploads/..." 或其他以 / 开头的情况
                        rel = oldUrl.replaceFirst("^/", "");
                    } else {
                        rel = oldUrl;
                    }

                    if (rel != null && rel.startsWith("users/avatars/")) {
                        Path oldPath = Paths.get(uploadPath, rel).toAbsolutePath().normalize();
                        Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
                        // 安全校验：必须在上传根目录内
                        if (oldPath.startsWith(root)) {
                            Files.deleteIfExists(oldPath);
                        }
                    }
                } catch (Exception ignore) {
                    // 忽略旧文件删除异常，保证上传流程不受影响
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "系统错误: " + e.getMessage()));
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件不能为空"));
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("message", "只能上传图片文件"));
            }

            // 验证文件大小 (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件大小不能超过10MB"));
            }

            // 创建上传目录
            String imageDir = uploadPath + "/images";
            File dir = new File(imageDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            // 保存文件
            Path filePath = Paths.get(imageDir, filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件URL
            String fileUrl = "/uploads/images/" + filename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("url", fileUrl);
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "文件上传失败"));
        }
    }
}