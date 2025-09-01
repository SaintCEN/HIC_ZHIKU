package com.example.backend.mapper;

import com.example.backend.entity.Share;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShareMapper {
    
    @Select({
        "<script>",
        "SELECT id, title, content, category, text_url AS textUrl, view_count AS viewCount, created_at AS createdAt",
        "FROM shares",
        "<where>",
        "<if test='search != null and search != \"\"'>",
        "AND (title LIKE '%${search}%' OR content LIKE '%${search}%')",
        "</if>",
        "</where>",
        "ORDER BY created_at DESC, id DESC",
        "LIMIT ${limit} OFFSET ${offset}",
        "</script>"
    })
    List<Share> list(@Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);
    
    @Select({
        "<script>",
        "SELECT COUNT(*) FROM shares",
        "<where>",
        "<if test='search != null and search != \"\"'>",
        "AND (title LIKE '%${search}%' OR content LIKE '%${search}%')",
        "</if>",
        "</where>",
        "</script>"
    })
    long count(@Param("search") String search);
    
    @Select("SELECT id, title, content, category, text_url AS textUrl, view_count AS viewCount, created_at AS createdAt FROM shares WHERE id = #{id}")
    Share getById(@Param("id") Long id);
    
    @Update("UPDATE shares SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    @Insert("INSERT INTO shares (title, content, category, text_url, view_count, created_at) VALUES (#{title}, #{content}, #{category}, #{textUrl}, #{viewCount}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Share share);
    
    // 测试方法
    @Select("SELECT id, title, content, category, text_url AS textUrl, view_count AS viewCount, created_at AS createdAt FROM shares")
    List<Share> testDirectQuery();
    
    @Select("SELECT id, title, content, category, text_url AS textUrl, view_count AS viewCount, created_at AS createdAt FROM shares WHERE category = #{category}")
    List<Share> testParameterQuery(@Param("category") String category);
    
    @Select("SELECT id, title, content, category, text_url AS textUrl, view_count AS viewCount, created_at AS createdAt FROM shares WHERE category = #{category} ORDER BY created_at DESC, id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Share> testParameterWithLimitQuery(@Param("offset") int offset, @Param("limit") int limit, @Param("category") String category);
}