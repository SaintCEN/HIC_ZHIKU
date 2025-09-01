package com.example.backend.mapper;

import com.example.backend.entity.UserLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserLikeMapper {

    @Insert("INSERT INTO user_likes (user_id, resource_type, resource_id, created_at) " +
            "VALUES (#{userId}, #{resourceType}, #{resourceId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUserLike(UserLike userLike);

    @Delete("DELETE FROM user_likes WHERE user_id = #{userId} AND resource_type = #{resourceType} AND resource_id = #{resourceId}")
    int deleteUserLike(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);

    @Select("SELECT COUNT(*) FROM user_likes WHERE user_id = #{userId} AND resource_type = #{resourceType} AND resource_id = #{resourceId}")
    int checkUserLike(@Param("userId") Long userId, @Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);

    @Select("SELECT COUNT(*) FROM user_likes WHERE resource_type = #{resourceType} AND resource_id = #{resourceId}")
    int getLikeCount(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);
}