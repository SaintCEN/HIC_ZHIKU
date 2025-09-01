package com.example.backend.service.impl;

import com.example.backend.entity.User;
import com.example.backend.entity.UserLike;
import com.example.backend.mapper.UserLikeMapper;
import com.example.backend.service.UserLikeService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserLikeServiceImpl implements UserLikeService {

    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean toggleLike(String username, String resourceType, Long resourceId) {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Long userId = user.getId();
        // 检查是否已经点赞
        int exists = userLikeMapper.checkUserLike(userId, resourceType, resourceId);
        
        if (exists > 0) {
            // 已点赞，取消点赞
            userLikeMapper.deleteUserLike(userId, resourceType, resourceId);
            return false; // 返回false表示取消点赞
        } else {
            // 未点赞，添加点赞
            UserLike userLike = new UserLike(userId, resourceType, resourceId);
            userLike.setCreatedAt(LocalDateTime.now());
            userLikeMapper.insertUserLike(userLike);
            return true; // 返回true表示点赞成功
        }
    }

    @Override
    public boolean isLiked(String username, String resourceType, Long resourceId) {
        User user = userService.getByUsername(username);
        if (user == null) {
            return false;
        }
        return userLikeMapper.checkUserLike(user.getId(), resourceType, resourceId) > 0;
    }

    @Override
    public int getLikeCount(String resourceType, Long resourceId) {
        return userLikeMapper.getLikeCount(resourceType, resourceId);
    }
}