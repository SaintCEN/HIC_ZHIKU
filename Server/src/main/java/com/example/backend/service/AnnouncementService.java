package com.example.backend.service;

import com.example.backend.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    List<Announcement> listAll(int page, int size);
    List<Announcement> latest(int limit);
    Announcement getById(Long id, boolean increaseView);
}