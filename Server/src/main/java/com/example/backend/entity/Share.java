package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 师兄师姐说实体类
 */
@Data
public class Share {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String textUrl;
    private Integer viewCount;
    private LocalDateTime createdAt;
}