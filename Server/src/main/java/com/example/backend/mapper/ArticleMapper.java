package com.example.backend.mapper;

import com.example.backend.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("SELECT id, title, summary, cover_image AS coverImage, view_count AS viewCount, link_url AS linkUrl, publish_time AS publishTime, created_at AS createdAt FROM articles ORDER BY publish_time DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Article> listAll(@Param("offset") int offset, @Param("limit") int limit);

    @Select({
            "<script>",
            "SELECT id, title, summary, cover_image AS coverImage, view_count AS viewCount, link_url AS linkUrl, publish_time AS publishTime, created_at AS createdAt",
            " FROM articles",
            " WHERE (title LIKE '%${search}%' OR summary LIKE '%${search}%')",
            " ORDER BY publish_time DESC, id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Article> listBySearch(@Param("search") String search, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM articles")
    long countAll();

    @Select("SELECT COUNT(*) FROM articles WHERE (title LIKE '%${search}%' OR summary LIKE '%${search}%')")
    long countBySearch(@Param("search") String search);

    default List<Article> list(int offset, int limit, String search) {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        if (hasSearch) {
            return listBySearch(search, offset, limit);
        }
        return listAll(offset, limit);
    }

    default long count(String search) {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        if (hasSearch) {
            return countBySearch(search);
        }
        return countAll();
    }

    @Select("SELECT id, title, summary, cover_image AS coverImage, view_count AS viewCount, link_url AS linkUrl, publish_time AS publishTime, created_at AS createdAt FROM articles WHERE id = #{id}")
    Article findById(@Param("id") Long id);

    @Update("UPDATE articles SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    // 测试方法：简化的搜索查询
    @Select("SELECT id, title, summary FROM articles WHERE title LIKE CONCAT('%', #{search}, '%') ORDER BY id LIMIT 5")
    List<Article> testSearch(@Param("search") String search);
}



