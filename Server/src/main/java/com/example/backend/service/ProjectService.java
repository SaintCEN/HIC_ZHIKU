package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Project;

import java.util.List;

public interface ProjectService {
    
    /**
     * 分页查询项目列表
     */
    PageResponse<Project> getProjects(int page, int size, String category, String search);
    
    /**
     * 根据ID获取项目详情
     */
    Project getProjectById(Long id);
    
    /**
     * 增加项目浏览次数
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加项目收藏数
     */
    void incrementStarCount(Long id);
    
    /**
     * 创建项目
     */
    Project createProject(Project project);
    
    /**
     * 更新项目
     */
    Project updateProject(Project project);
    
    /**
     * 删除项目
     */
    void deleteProject(Long id);
    
    /**
     * 获取所有项目分类
     */
    List<String> getAllCategories();
    
    /**
     * 根据作者获取项目列表（分页）
     */
    PageResponse<Project> getProjectsByAuthor(Long authorId, int page, int size);
}