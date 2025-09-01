package com.example.backend.mapper;

import com.example.backend.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Select("SELECT id, title, summary, content, cover_url AS coverUrl, view_count AS viewCount, publish_time AS publishTime FROM announcements ORDER BY publish_time DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Announcement> list(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT id, title, summary, content, cover_url AS coverUrl, view_count AS viewCount, publish_time AS publishTime FROM announcements ORDER BY publish_time DESC, id DESC LIMIT #{limit}")
    List<Announcement> latest(@Param("limit") int limit);

    @Select("SELECT id, title, summary, content, cover_url AS coverUrl, view_count AS viewCount, publish_time AS publishTime FROM announcements WHERE id = #{id}")
    Announcement findById(@Param("id") Long id);

    @Update("UPDATE announcements SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    @Insert("INSERT INTO announcements(title, summary, content, cover_url, view_count, publish_time) VALUES(#{title}, #{summary}, #{content}, #{coverUrl}, #{viewCount}, #{publishTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement a);

    // 按搜索关键词查询公告
    @Select("SELECT id, title, summary, content, cover_url AS coverUrl, view_count AS viewCount, publish_time AS publishTime FROM announcements WHERE (title LIKE CONCAT('%', #{search}, '%') OR summary LIKE CONCAT('%', #{search}, '%')) ORDER BY publish_time DESC, id DESC LIMIT ${limit} OFFSET ${offset}")
    List<Announcement> listBySearch(@Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);

    // 按搜索关键词统计公告数量
    @Select("SELECT COUNT(*) FROM announcements WHERE (title LIKE CONCAT('%', #{search}, '%') OR summary LIKE CONCAT('%', #{search}, '%'))")
    long countBySearch(@Param("search") String search);
}