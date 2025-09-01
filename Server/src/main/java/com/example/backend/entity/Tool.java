package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Tool {
    private Long id;
    private String name;
    private String description;
    private String category;

    private String toolUrl;
    private Integer eyeCount;
    private LocalDateTime createdAt;

    // 构造函数
    public Tool() {}

    public Tool(String name, String description, String category, String toolUrl) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.toolUrl = toolUrl;
        this.eyeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public String getToolUrl() {
        return toolUrl;
    }

    public void setToolUrl(String toolUrl) {
        this.toolUrl = toolUrl;
    }

    public Integer getEyeCount() {
        return eyeCount;
    }

    public void setEyeCount(Integer eyeCount) {
        this.eyeCount = eyeCount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}