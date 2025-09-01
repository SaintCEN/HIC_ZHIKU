package com.example.backend.service.impl;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Tool;
import com.example.backend.mapper.ToolMapper;
import com.example.backend.service.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolMapper toolMapper;

    @Override
    public PageResponse<Tool> list(int page, int size, String category, String search) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;
        List<Tool> data = toolMapper.list(offset, size, category, search);
        long total = toolMapper.count(category, search);
        return new PageResponse<>(data, total, page, size);
    }

    @Override
    public Tool getById(Long id, boolean increaseEyeCount) {
        if (increaseEyeCount) {
            toolMapper.incrementEyeCount(id);
        }
        return toolMapper.findById(id);
    }
}