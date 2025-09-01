package com.example.backend.service;

import com.example.backend.dto.FavoriteDtos;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Project;
import com.example.backend.entity.User;
import com.example.backend.entity.UserFavorite;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    private final UserFavoriteMapper favoriteMapper;
    private final UserService userService;
    private final ProjectService projectService;

    public UserFavoriteService(UserFavoriteMapper favoriteMapper, UserService userService, @Lazy ProjectService projectService) {
        this.favoriteMapper = favoriteMapper;
        this.userService = userService;
        this.projectService = projectService;
    }

    public void addFavorite(String username, Long resourceId, String resourceTitle, String resourceUrl) {
        User user = userService.getByUsername(username);

        // 检查是否已经收藏
        if (favoriteMapper.existsByUserAndResource(user.getId(), resourceId)) {
            throw new BusinessException("已经收藏过该项目");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(user.getId());
        favorite.setResourceId(resourceId);
        favorite.setResourceTitle(resourceTitle);
        favorite.setResourceUrl(resourceUrl);

        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(String username, Long resourceId) {
        User user = userService.getByUsername(username);
        int deleted = favoriteMapper.deleteByUserAndResource(user.getId(), resourceId);
        if (deleted == 0) {
            throw new BusinessException("收藏记录不存在");
        }
    }

    public void removeFavoriteById(String username, Long favoriteId) {
        User user = userService.getByUsername(username);
        int deleted = favoriteMapper.deleteByIdAndUser(favoriteId, user.getId());
        if (deleted == 0) {
            throw new BusinessException("收藏记录不存在");
        }
    }

    public PageResponse<FavoriteDtos.FavoriteResp> getFavorites(String username, int page, int size) {
        User user = userService.getByUsername(username);
        int offset = (page - 1) * size;

        List<UserFavorite> favorites = favoriteMapper.findByUserIdWithPaging(user.getId(), offset, size);
        long total = favoriteMapper.countByUserId(user.getId());

        List<FavoriteDtos.FavoriteResp> favoriteResps = favorites.stream()
                .map(this::toFavoriteResp)
                .collect(Collectors.toList());

        return new PageResponse<>(favoriteResps, total, page, size);
    }

    public boolean isFavorited(String username, Long resourceId) {
        User user = userService.getByUsername(username);
        return favoriteMapper.existsByUserAndResource(user.getId(), resourceId);
    }

    // New: get favorite count of a resource
    public long getFavoriteCount(Long resourceId) {
        return favoriteMapper.countByResourceId(resourceId);
    }

    private FavoriteDtos.FavoriteResp toFavoriteResp(UserFavorite favorite) {
        FavoriteDtos.FavoriteResp resp = new FavoriteDtos.FavoriteResp();
        resp.setId(favorite.getId());
        resp.setResourceId(favorite.getResourceId());
        resp.setResourceTitle(favorite.getResourceTitle());
        resp.setResourceUrl(favorite.getResourceUrl());
        resp.setCreatedAt(favorite.getCreatedAt());
        
        // 获取项目详细信息
        try {
            Project project = projectService.getProjectById(favorite.getResourceId());
            if (project != null) {
                resp.setResourceDescription(project.getDescription());
                resp.setAuthorName(project.getAuthorName());
                resp.setAuthorAvatar(project.getAuthorAvatar());
                resp.setViewCount(project.getViewCount());
                resp.setStarCount(project.getStarCount());
                resp.setCategory(project.getCategory());
                resp.setGithubUrl(project.getGithubUrl());
                // demoUrl字段在Project实体中不存在，暂时设为null
                resp.setDemoUrl(null);
                
                // 如果项目信息中的作者信息为空，设置默认值
                if (resp.getAuthorName() == null) {
                    resp.setAuthorName("未知用户");
                }
                if (resp.getAuthorAvatar() == null) {
                    resp.setAuthorAvatar("/default-avatar.png");
                }
            } else {
                System.err.println("Project not found for favorite: " + favorite.getResourceId());
            }
        } catch (Exception e) {
            // 如果获取项目信息失败，继续返回基本信息
            System.err.println("Failed to get project details for favorite: " + favorite.getResourceId() + ", error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resp;
    }
}