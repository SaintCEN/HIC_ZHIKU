package com.example.backend.mapper;

import org.apache.ibatis.annotations.Param;

public class UserFavoriteMapperProvider {
    
    public String findByUserIdWithPaging(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size) {
        return "SELECT * FROM user_favorites WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT " + offset + ", " + size;
    }
}