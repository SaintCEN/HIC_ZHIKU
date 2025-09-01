package com.example.backend.mapper;

import com.example.backend.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    @Insert("INSERT INTO user_favorites(user_id, resource_id, resource_title, resource_url) " +
            "VALUES(#{userId}, #{resourceId}, #{resourceTitle}, #{resourceUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFavorite favorite);

    @Delete("DELETE FROM user_favorites WHERE user_id = #{userId} AND resource_id = #{resourceId}")
    int deleteByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Delete("DELETE FROM user_favorites WHERE id = #{favoriteId} AND user_id = #{userId}")
    int deleteByIdAndUser(@Param("favoriteId") Long favoriteId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) > 0 FROM user_favorites WHERE user_id = #{userId} AND resource_id = #{resourceId}")
    boolean existsByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @SelectProvider(type = UserFavoriteMapperProvider.class, method = "findByUserIdWithPaging")
    List<UserFavorite> findByUserIdWithPaging(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user_favorites WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    // New: count favorites by resource id
    @Select("SELECT COUNT(*) FROM user_favorites WHERE resource_id = #{resourceId}")
    long countByResourceId(@Param("resourceId") Long resourceId);
}