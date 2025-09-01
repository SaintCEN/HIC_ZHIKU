package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String textUrl;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status; // 0-未开始，1-正在进行，2-已结束
    private Integer viewCount;
}