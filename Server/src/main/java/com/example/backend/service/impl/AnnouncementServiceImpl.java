package com.example.backend.service.impl;

import com.example.backend.entity.Announcement;
import com.example.backend.mapper.AnnouncementMapper;
import com.example.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> listAll(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;
        return announcementMapper.list(offset, size);
    }

    @Override
    public List<Announcement> latest(int limit) {
        if (limit <= 0) limit = 2;
        return announcementMapper.latest(limit);
    }

    @Override
    public Announcement getById(Long id, boolean increaseView) {
        if (increaseView) {
            announcementMapper.incrementViewCount(id);
        }
        return announcementMapper.findById(id);
    }
}