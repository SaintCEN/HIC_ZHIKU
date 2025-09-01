package com.example.backend.mapper;

import com.example.backend.entity.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {

    @Select({
            "<script>",
            "SELECT p.id, p.title, p.description, p.detailed_description AS detailedDescription,",
            " p.category, p.github_url AS githubUrl, p.view_count AS viewCount,",
            " p.star_count AS starCount, p.author_id AS authorId,",
            " p.created_at AS createdAt, COALESCE(u.nickname, u.username) AS authorName, u.avatar_url AS authorAvatar",
            " FROM projects p",
            " LEFT JOIN users u ON p.author_id = u.id",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND p.category = '${category}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (p.title LIKE '%${search}%' OR p.description LIKE '%${search}%')",
            " </if>",
            " ORDER BY p.created_at DESC, p.id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Project> list(@Param("offset") Integer offset, @Param("limit") Integer limit,
                      @Param("category") String category, @Param("search") String search);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM projects p",
            " WHERE 1 = 1",
            " <if test='category != null and category != \"\" and category != \"全部\"'>",
            "   AND p.category = '${category}'",
            " </if>",
            " <if test='search != null and search != \"\"'>",
            "   AND (p.title LIKE '%${search}%' OR p.description LIKE '%${search}%')",
            " </if>",
            "</script>"
    })
    long count(@Param("category") String category, @Param("search") String search);

    @Select({
            "SELECT p.id, p.title, p.description, p.detailed_description AS detailedDescription,",
            " p.category, p.github_url AS githubUrl, p.view_count AS viewCount,",
            " p.star_count AS starCount, p.author_id AS authorId,",
            " p.created_at AS createdAt, COALESCE(u.nickname, u.username) AS authorName, u.avatar_url AS authorAvatar",
            " FROM projects p",
            " LEFT JOIN users u ON p.author_id = u.id",
            " WHERE p.id = #{id}"
    })
    Project findById(@Param("id") Long id);

    @Update("UPDATE projects SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    @Update("UPDATE projects SET star_count = star_count + 1 WHERE id = #{id}")
    int incrementStarCount(@Param("id") Long id);

    @Insert({
            "INSERT INTO projects(title, description, detailed_description, category,",
            " github_url, view_count, star_count, author_id, created_at)",
            " VALUES(#{title}, #{description}, #{detailedDescription}, #{category},",
            " #{githubUrl}, #{viewCount}, #{starCount}, #{authorId}, #{createdAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Project project);

    @Update({
            "<script>",
            "UPDATE projects SET",
            " title = #{title},",
            " description = #{description},",
            " <if test='detailedDescription != null'>detailed_description = #{detailedDescription},</if>",
            " <if test='detailedDescription == null'>detailed_description = NULL,</if>",
            " category = #{category},",
            " github_url = #{githubUrl}",
            " WHERE id = #{id}",
            "</script>"
    })
    int update(Project project);

    @Delete("DELETE FROM projects WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT DISTINCT category FROM projects ORDER BY category")
    List<String> getAllCategories();

    @Select({
            "SELECT p.id, p.title, p.description, p.detailed_description AS detailedDescription,",
            " p.category, p.github_url AS githubUrl, p.view_count AS viewCount,",
            " p.star_count AS starCount, p.author_id AS authorId,",
            " p.created_at AS createdAt, COALESCE(u.nickname, u.username) AS authorName, u.avatar_url AS authorAvatar",
            " FROM projects p",
            " LEFT JOIN users u ON p.author_id = u.id",
            " WHERE p.author_id = #{authorId}",
            " ORDER BY p.created_at DESC, p.id DESC",
            " LIMIT ${limit} OFFSET ${offset}"
    })
    List<Project> listByAuthor(@Param("authorId") Long authorId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select({
            "SELECT COUNT(*) FROM projects p",
            " WHERE p.author_id = #{authorId}"
    })
    long countByAuthor(@Param("authorId") Long authorId);
}