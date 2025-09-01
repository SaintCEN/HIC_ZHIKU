package com.example.backend.service.impl;

import com.example.backend.entity.Resource;
import com.example.backend.mapper.ResourceMapper;
import com.example.backend.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;

    @Override
    public List<Resource> listAll(int page, int size, String category, String type, String search) {
        int offset = (page - 1) * size;
        int limit = size;
        return resourceMapper.list(offset, limit, category, type, search);
    }

    @Override
    public long count(String category, String type, String search) {
        return resourceMapper.count(category, type, search);
    }

    @Override
    public Resource getById(Long id, boolean increaseView) {
        if (increaseView) {
            resourceMapper.incrementEyeCount(id);
        }
        return resourceMapper.findById(id);
    }

    @Override
    public Integer testDynamicCountFixed(String category) {
        return resourceMapper.testDynamicCountFixed(category);
    }

    @Override
    public List<Map<String, Object>> getDbData() {
        return resourceMapper.getDbData();
    }
}