package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Project;
import com.example.backend.entity.User;
import com.example.backend.service.ProjectService;
import com.example.backend.service.UserLikeService;
import com.example.backend.service.UserFavoriteService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserLikeService userLikeService;
    
    @Autowired
    private UserFavoriteService userFavoriteService;
    
    @Autowired
    private UserService userService;
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadPath;

    /**
     * 获取项目列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Project>>> getProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        
        PageResponse<Project> projects = projectService.getProjects(page, size, category, search);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    /**
     * 根据ID获取项目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.ok(ApiResponse.error("项目不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * 增加项目浏览次数
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        projectService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }



    /**
     * 切换项目收藏状态
     */
    @PostMapping("/{id}/star")
    public ResponseEntity<?> toggleStar(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            
            // 检查是否已收藏
            boolean isFavorited = userFavoriteService.isFavorited(username, id);
            
            if (isFavorited) {
                // 取消收藏
                userFavoriteService.removeFavorite(username, id);
            } else {
                // 添加收藏
                Project project = projectService.getProjectById(id);
                if (project == null) {
                    return ResponseEntity.ok(ApiResponse.error("项目不存在"));
                }
                userFavoriteService.addFavorite(username, id, project.getTitle(), 
                    "/project/" + id);
            }
            
            // 重新计算状态与数量
            boolean newStatus = userFavoriteService.isFavorited(username, id);
            long starCount = userFavoriteService.getFavoriteCount(id);
            Map<String, Object> data = new HashMap<>();
            data.put("isStarred", newStatus);
            data.put("starCount", starCount);
            String message = newStatus ? "收藏成功" : "取消收藏成功";
            return ResponseEntity.ok(ApiResponse.success(message, data));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 创建项目
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam(value = "githubUrl", required = false) String githubUrl,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            Authentication authentication) {
        
        try {
            Project project = new Project();
            project.setTitle(title);
            project.setDescription(description);
            project.setCategory(category);
            project.setGithubUrl(githubUrl);
            project.setViewCount(0); // 初始化浏览量
            project.setStarCount(0); // 初始化收藏量
            project.setCreatedAt(LocalDateTime.now()); // 设置创建时间
            
            // 设置作者信息
            if (authentication != null) {
                String username = authentication.getName();
                User user = userService.getByUsername(username);
                if (user != null) {
                    project.setAuthorId(user.getId());
                }
            }
            
            // 处理文档文件上传
            if (documentFile != null && !documentFile.isEmpty()) {
                String documentUrl = uploadDocument(documentFile);
                project.setDetailedDescription(documentUrl);
            }
            
            Project createdProject = projectService.createProject(project);
            return ResponseEntity.ok(ApiResponse.success(createdProject));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.error("项目创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传文档文件
     */
    private String uploadDocument(MultipartFile file) throws IOException {
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".md")) {
            throw new IllegalArgumentException("只支持 .md 格式的文件");
        }
        
        // 创建上传目录
        String subDir = "projects";
        Path uploadDir = Paths.get(uploadPath, subDir);
        Files.createDirectories(uploadDir);
        
        // 生成唯一文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        
        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回相对路径
        return "uploads/" + subDir + "/" + filename;
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam(value = "githubUrl", required = false) String githubUrl,
            @RequestParam(value = "documentFile", required = false) MultipartFile documentFile,
            Authentication authentication) {
        
        try {
            // 获取现有项目
            Project existingProject = projectService.getProjectById(id);
            if (existingProject == null) {
                return ResponseEntity.ok(ApiResponse.error("项目不存在"));
            }
            
            // 检查权限（只有作者可以编辑）
            if (authentication == null) {
                return ResponseEntity.ok(ApiResponse.error("请先登录"));
            }
            
            String username = authentication.getName();
            User user = userService.getByUsername(username);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.error("用户不存在"));
            }
            
            if (!user.getId().equals(existingProject.getAuthorId())) {
                return ResponseEntity.ok(ApiResponse.error("无权限编辑此项目"));
            }
            
            // 更新项目信息
            existingProject.setTitle(title);
            existingProject.setDescription(description);
            existingProject.setCategory(category);
            existingProject.setGithubUrl(githubUrl);
            
            // 处理文档文件上传
            if (documentFile != null && !documentFile.isEmpty()) {
                String documentUrl = uploadDocument(documentFile);
                existingProject.setDetailedDescription(documentUrl);
            }
            
            Project updatedProject = projectService.updateProject(existingProject);
            return ResponseEntity.ok(ApiResponse.success(updatedProject));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.error("项目更新失败: " + e.getMessage()));
        }
    }

    /**
     * 更新项目（JSON格式，不包含文件）
     */
    @PutMapping("/{id}/info")
    public ResponseEntity<ApiResponse<Project>> updateProjectInfo(@PathVariable Long id, @RequestBody Project project, Authentication authentication) {
        try {
            // 获取现有项目
            Project existingProject = projectService.getProjectById(id);
            if (existingProject == null) {
                return ResponseEntity.ok(ApiResponse.error("项目不存在"));
            }
            
            // 检查权限（只有作者可以编辑）
            if (authentication == null) {
                return ResponseEntity.ok(ApiResponse.error("请先登录"));
            }
            
            String username = authentication.getName();
            User user = userService.getByUsername(username);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.error("用户不存在"));
            }
            
            if (!user.getId().equals(existingProject.getAuthorId())) {
                return ResponseEntity.ok(ApiResponse.error("无权限编辑此项目"));
            }
            
            // 更新项目信息
            existingProject.setTitle(project.getTitle());
            existingProject.setDescription(project.getDescription());
            existingProject.setCategory(project.getCategory());
            existingProject.setGithubUrl(project.getGithubUrl());
            
            // 如果传入了detailedDescription，也更新它（用于移除文档）
            if (project.getDetailedDescription() != null) {
                existingProject.setDetailedDescription(project.getDetailedDescription());
            }
            
            Project updatedProject = projectService.updateProject(existingProject);
            return ResponseEntity.ok(ApiResponse.success(updatedProject));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.error("项目更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }



    /**
     * 检查用户对项目的收藏状态
     */
    @GetMapping("/{id}/star/status")
    public ResponseEntity<?> getStarStatus(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        
        boolean isStarred = userFavoriteService.isFavorited(username, id);
        long starCount = userFavoriteService.getFavoriteCount(id);
        
        Map<String, Object> data = new HashMap<>();
        data.put("isStarred", isStarred);
        data.put("starCount", starCount);
        
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 获取项目分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = projectService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * 获取用户自己的项目列表
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyProjects(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未认证"));
        }
        
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "用户不存在"));
        }
        
        PageResponse<Project> projects = projectService.getProjectsByAuthor(user.getId(), 1, 100);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "data", projects));
    }
}