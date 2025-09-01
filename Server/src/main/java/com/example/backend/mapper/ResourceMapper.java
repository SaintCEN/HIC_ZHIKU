package com.example.backend.mapper;

import com.example.backend.entity.Resource;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper {

    @Select({
            "<script>",
            "SELECT id, title, description, category, type, link_url AS resourceUrl, view_count AS eyeCount, created_at AS createdAt",
            " FROM resources",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND category = '${category}'",
            " </if>",
            " <if test='type != null and type != \"\" and type != \"全部\"'>",
            "   AND type = '${type}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (title LIKE '%${search}%' OR description LIKE '%${search}%')",
            " </if>",
            " ORDER BY created_at DESC, id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Resource> list(@Param("offset") int offset, @Param("limit") int limit,
                       @Param("category") String category, @Param("type") String type, 
                       @Param("search") String search);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM resources",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND category = '${category}'",
            " </if>",
            " <if test='type != null and type != \"\" and type != \"全部\"'>",
            "   AND type = '${type}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (title LIKE '%${search}%' OR description LIKE '%${search}%')",
            " </if>",
            "</script>"
    })
    long count(@Param("category") String category, @Param("type") String type, 
               @Param("search") String search);

    @Select("SELECT id, title, description, category, type, link_url AS resourceUrl, view_count AS eyeCount, created_at AS createdAt FROM resources WHERE id = #{id}")
    Resource findById(@Param("id") Long id);

    @Update("UPDATE resources SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementEyeCount(@Param("id") Long id);

    @Insert("INSERT INTO resources (title, description, category, type, link_url, view_count, created_at) " +
            "VALUES (#{title}, #{description}, #{category}, #{type}, #{resourceUrl}, #{eyeCount}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Resource resource);

    // 测试方法：直接查询software分类
    @Select("SELECT id, title, description, category, type, link_url AS resourceUrl, view_count AS eyeCount, created_at AS createdAt FROM resources WHERE category = 'software'")
    List<Resource> testSoftwareQuery();
    
    // 测试方法：带参数查询
    @Select("SELECT id, title, description, category, type, link_url AS resourceUrl, view_count AS eyeCount, created_at AS createdAt FROM resources WHERE category = #{category}")
    List<Resource> testCategoryQuery(@Param("category") String category);
    
    // 测试方法：简单的count查询
    @Select("SELECT COUNT(*) FROM resources WHERE category = #{category}")
    long testCountQuery(@Param("category") String category);
    
    // 查看所有不同的category值
    @Select("SELECT DISTINCT category FROM resources ORDER BY category")
    List<String> getAllCategories();
    
    // 查看所有resources的详细信息
    @Select("SELECT id, title, category, type FROM resources ORDER BY id")
    List<Resource> getAllResourcesInfo();
    
    // 测试动态SQL但使用固定条件
    @Select({
            "<script>",
            "SELECT COUNT(*) FROM resources",
            " WHERE category = #{category}",
            "</script>"
    })
    Integer testDynamicCountFixed(@Param("category") String category);

    @Select("SELECT id, category, CHAR_LENGTH(category) as len, HEX(category) as hex_value FROM resources WHERE id IN (7, 8)")
    List<Map<String, Object>> getDbData();



}