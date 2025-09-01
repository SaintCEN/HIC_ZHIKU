package com.example.backend.mapper;

import com.example.backend.entity.Competition;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompetitionMapper {

    @Select({
            "<script>",
            "SELECT id, title, description, status, start_time AS startTime,",
            " official_link AS officialLink, view_count AS viewCount, created_at AS createdAt",
            " FROM competitions",
            " <where>",
            "   <if test=\"status != null and status != ''\">",
            "     AND status = #{status}",
            "   </if>",
            "   <if test=\"search != null and search != ''\">",
            "     AND (title LIKE '%${search}%' OR description LIKE '%${search}%')",
            "   </if>",
            " </where>",
            " ORDER BY start_time DESC, id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Competition> list(@Param("offset") int offset, @Param("limit") int limit,
                           @Param("status") String status, @Param("search") String search);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM competitions",
            " <where>",
            "   <if test=\"status != null and status != ''\">",
            "     AND status = #{status}",
            "   </if>",
            "   <if test=\"search != null and search != ''\">",
            "     AND (title LIKE '%${search}%' OR description LIKE '%${search}%')",
            "   </if>",
            " </where>",
            "</script>"
    })
    long count(@Param("status") String status, @Param("search") String search);

    @Select("SELECT id, title, description, status, start_time AS startTime, official_link AS officialLink, view_count AS viewCount, created_at AS createdAt FROM competitions WHERE id = #{id}")
    Competition findById(@Param("id") Long id);

    @Update("UPDATE competitions SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    // 按搜索关键词查询竞赛
    @Select("SELECT id, title, description, status, start_time AS startTime, official_link AS officialLink, view_count AS viewCount, created_at AS createdAt FROM competitions WHERE (title LIKE '%${search}%' OR description LIKE '%${search}%') ORDER BY start_time DESC, id DESC LIMIT #{limit} OFFSET #{offset}")
    List<Competition> listBySearch(@Param("offset") int offset, @Param("limit") int limit, @Param("search") String search);

    // 按搜索关键词统计竞赛数量
    @Select("SELECT COUNT(*) FROM competitions WHERE (title LIKE '%${search}%' OR description LIKE '%${search}%')")
    long countBySearch(@Param("search") String search);
}