package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Tool;

public interface ToolService {
    PageResponse<Tool> list(int page, int size, String category, String search);
    Tool getById(Long id, boolean increaseEyeCount);
}