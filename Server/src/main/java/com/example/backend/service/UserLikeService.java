package com.example.backend.service;

public interface UserLikeService {
    
    /**
     * 切换用户点赞状态
     * @param username 用户名
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return true表示点赞，false表示取消点赞
     */
    boolean toggleLike(String username, String resourceType, Long resourceId);
    
    /**
     * 检查用户是否已点赞
     * @param username 用户名
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return true表示已点赞，false表示未点赞
     */
    boolean isLiked(String username, String resourceType, Long resourceId);
    
    /**
     * 获取资源的点赞数
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 点赞数
     */
    int getLikeCount(String resourceType, Long resourceId);
}