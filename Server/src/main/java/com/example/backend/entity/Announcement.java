package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long id;
    private String title;
    private String summary;
    /**
     * 内容字段将用于存储 Markdown 文件的路径（例如：/uploads/announcements/xxx.md）
     */
    private String content;
    private String coverUrl;
    private Integer viewCount;
    private LocalDateTime publishTime;
}