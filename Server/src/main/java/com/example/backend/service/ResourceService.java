package com.example.backend.service;

import com.example.backend.entity.Resource;

import java.util.List;
import java.util.Map;

public interface ResourceService {
    List<Resource> listAll(int page, int size, String category, String type, String search);
    long count(String category, String type, String search);
    Resource getById(Long id, boolean increaseView);

    Integer testDynamicCountFixed(String category);
    
    List<Map<String, Object>> getDbData();

}