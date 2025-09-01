package com.example.backend.service.impl;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Share;
import com.example.backend.mapper.ShareMapper;
import com.example.backend.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShareServiceImpl implements ShareService {

    private final ShareMapper shareMapper;

    @Autowired
    public ShareServiceImpl(ShareMapper shareMapper) {
        this.shareMapper = shareMapper;
    }

    @Override
    public PageResponse<Share> list(int page, int size, String search) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;
        
        List<Share> data = shareMapper.list(offset, size, search);
        long total = shareMapper.count(search);
        
        return new PageResponse<>(data, total, page, size);
    }

    @Override
    public Share getById(Long id, boolean increaseViewCount) {
        if (increaseViewCount) {
            shareMapper.incrementViewCount(id);
        }
        return shareMapper.getById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        shareMapper.incrementViewCount(id);
    }

    @Override
    public List<Share> testDirectQuery() {
        return shareMapper.testDirectQuery();
    }

    @Override
    public List<Share> testParameterQuery(String category) {
        return shareMapper.testParameterQuery(category);
    }

    @Override
    public List<Share> testParameterWithLimitQuery(int offset, int limit, String category) {
        return shareMapper.testParameterWithLimitQuery(offset, limit, category);
    }

    @Override
    public Share create(Share share) {
        // 设置创建时间
        if (share.getCreatedAt() == null) {
            share.setCreatedAt(java.time.LocalDateTime.now());
        }
        // 设置初始浏览次数
        if (share.getViewCount() == null) {
            share.setViewCount(0);
        }
        
        shareMapper.insert(share);
        return share;
    }
}