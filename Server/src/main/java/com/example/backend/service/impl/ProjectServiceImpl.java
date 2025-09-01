package com.example.backend.service.impl;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Project;
import com.example.backend.mapper.ProjectMapper;
import com.example.backend.service.ProjectService;
import com.example.backend.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserFavoriteService userFavoriteService;

    @Override
    public PageResponse<Project> getProjects(int page, int size, String category, String search) {
        int offset = (page - 1) * size;
        
        List<Project> projects = projectMapper.list(offset, size, category, search);
        long total = projectMapper.count(category, search);
        
        // 为每个项目设置正确的收藏数
        for (Project project : projects) {
            long starCount = userFavoriteService.getFavoriteCount(project.getId());
            project.setStarCount((int) starCount);
        }
        
        PageResponse<Project> response = new PageResponse<>();
        response.setData(projects);
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(size);
        response.setTotalPages((int) Math.ceil((double) total / size));
        
        return response;
    }

    @Override
    public Project getProjectById(Long id) {
        return projectMapper.findById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        projectMapper.incrementViewCount(id);
    }

    @Override
    public void incrementStarCount(Long id) {
        projectMapper.incrementStarCount(id);
    }

    @Override
    public Project createProject(Project project) {
        projectMapper.insert(project);
        return project;
    }

    @Override
    public Project updateProject(Project project) {
        projectMapper.update(project);
        return projectMapper.findById(project.getId());
    }

    @Override
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }

    @Override
    public List<String> getAllCategories() {
        return projectMapper.getAllCategories();
    }

    @Override
    public PageResponse<Project> getProjectsByAuthor(Long authorId, int page, int size) {
        int offset = (page - 1) * size;
        
        List<Project> projects = projectMapper.listByAuthor(authorId, offset, size);
        long total = projectMapper.countByAuthor(authorId);
        
        PageResponse<Project> response = new PageResponse<>();
        response.setData(projects);
        response.setTotal(total);
        response.setPage(page);
        response.setPageSize(size);
        response.setTotalPages((int) Math.ceil((double) total / size));
        
        return response;
    }
}