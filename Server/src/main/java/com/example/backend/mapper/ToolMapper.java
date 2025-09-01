package com.example.backend.mapper;

import com.example.backend.entity.Tool;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ToolMapper {

    // 查询所有工具
    @Select("SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt FROM tools ORDER BY created_at DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Tool> listAll(@Param("offset") int offset, @Param("limit") int limit);

    // 按分类查询工具
    @Select("SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt FROM tools WHERE category = #{category} ORDER BY created_at DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Tool> listByCategory(@Param("offset") int offset, @Param("limit") int limit, @Param("category") String category);

    // 按搜索关键词查询工具
    @Select("SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt FROM tools WHERE (name LIKE '%${search}%' OR description LIKE '%${search}%') ORDER BY created_at DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Tool> listBySearch(@Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);

    // 按分类和搜索关键词查询工具
    @Select("SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt FROM tools WHERE category = #{category} AND (name LIKE '%${search}%' OR description LIKE '%${search}%') ORDER BY created_at DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Tool> listByCategoryAndSearch(@Param("offset") int offset, @Param("limit") int limit, @Param("category") String category, @Param("search") String search);

    // 统计所有工具数量
    @Select("SELECT COUNT(*) FROM tools")
    long countAll();

    // 按分类统计工具数量
    @Select("SELECT COUNT(*) FROM tools WHERE category = #{category}")
    long countByCategory(@Param("category") String category);

    // 按搜索关键词统计工具数量
    @Select("SELECT COUNT(*) FROM tools WHERE (name LIKE '%${search}%' OR description LIKE '%${search}%')")
    long countBySearch(@Param("search") String search);

    // 按分类和搜索关键词统计工具数量
    @Select("SELECT COUNT(*) FROM tools WHERE category = #{category} AND (name LIKE '%${search}%' OR description LIKE '%${search}%')")
    long countByCategoryAndSearch(@Param("category") String category, @Param("search") String search);

    // 使用动态SQL实现，参考ResourceMapper的方式
    @Select({
            "<script>",
            "SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt",
            " FROM tools",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND category = '${category}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (name LIKE '%${search}%' OR description LIKE '%${search}%')",
            " </if>",
            " ORDER BY created_at DESC, id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Tool> list(@Param("offset") int offset, @Param("limit") int limit,
                   @Param("category") String category, @Param("search") String search);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM tools",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND category = '${category}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (name LIKE '%${search}%' OR description LIKE '%${search}%')",
            " </if>",
            "</script>"
    })
    long count(@Param("category") String category, @Param("search") String search);

    @Select("SELECT id, name, description, category, tool_url AS toolUrl, eye_count AS eyeCount, created_at AS createdAt FROM tools WHERE id = #{id}")
    Tool findById(@Param("id") Long id);

    @Update("UPDATE tools SET eye_count = eye_count + 1 WHERE id = #{id}")
    int incrementEyeCount(@Param("id") Long id);


}