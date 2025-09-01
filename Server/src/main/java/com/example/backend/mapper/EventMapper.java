package com.example.backend.mapper;

import com.example.backend.entity.Event;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EventMapper {

    @Select({
            "<script>",
            "SELECT id, title, description, image_url AS imageUrl, text_url AS textUrl, location,",
            " start_time AS startTime, end_time AS endTime, view_count AS viewCount",
            " FROM events",
            " <where>",
            "   <if test='type != null and type != \"\"'>",
            "     <choose>",
            "       <when test='type == \"upcoming\"'> AND start_time &gt; NOW() </when>",
            "       <when test='type == \"ongoing\"'> AND start_time &lt;= NOW() AND end_time &gt;= NOW() </when>",
            "       <when test='type == \"completed\"'> AND end_time &lt; NOW() </when>",
            "     </choose>",
            "   </if>",
            "   <if test='search != null and search != \"\"'>",
            "     AND (title LIKE CONCAT('%', #{search}, '%') OR description LIKE CONCAT('%', #{search}, '%'))",
            "   </if>",
            " </where>",
            " ORDER BY start_time DESC, id DESC",
            " LIMIT ${limit} OFFSET ${offset}",
            "</script>"
    })
    List<Event> list(@Param("offset") int offset, @Param("limit") int limit,
                     @Param("type") String type, @Param("search") String search);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM events",
            " <where>",
            "   <if test='type != null and type != \"\"'>",
            "     <choose>",
            "       <when test='type == \"upcoming\"'> AND start_time &gt; NOW() </when>",
            "       <when test='type == \"ongoing\"'> AND start_time &lt;= NOW() AND end_time &gt;= NOW() </when>",
            "       <when test='type == \"completed\"'> AND end_time &lt; NOW() </when>",
            "     </choose>",
            "   </if>",
            "   <if test='search != null and search != \"\"'>",
            "     AND (title LIKE CONCAT('%', #{search}, '%') OR description LIKE CONCAT('%', #{search}, '%'))",
            "   </if>",
            " </where>",
            "</script>"
    })
    long count(@Param("type") String type, @Param("search") String search);

    @Select("SELECT id, title, description, image_url AS imageUrl, text_url AS textUrl, location, start_time AS startTime, end_time AS endTime, view_count AS viewCount FROM events ORDER BY start_time DESC, id DESC LIMIT #{limit}")
    List<Event> latest(@Param("limit") int limit);

    @Select("SELECT id, title, description, image_url AS imageUrl, text_url AS textUrl, location, start_time AS startTime, end_time AS endTime, view_count AS viewCount FROM events WHERE id = #{id}")
    Event findById(@Param("id") Long id);

    @Update("UPDATE events SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    @Insert("INSERT INTO events(title, description, image_url, text_url, location, start_time, end_time, view_count) VALUES(#{title}, #{description}, #{imageUrl}, #{textUrl}, #{location}, #{startTime}, #{endTime}, #{viewCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Event e);
}