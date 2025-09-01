package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Share;
import java.util.List;

public interface ShareService {
    PageResponse<Share> list(int page, int size, String search);
    Share getById(Long id, boolean increaseViewCount);
    void incrementViewCount(Long id);
    Share create(Share share);
    
    // 测试方法
    List<Share> testDirectQuery();
    List<Share> testParameterQuery(String category);
    List<Share> testParameterWithLimitQuery(int offset, int limit, String category);
}