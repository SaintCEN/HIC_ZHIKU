package com.example.backend.service;

import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Competition;

public interface CompetitionService {
    PageResponse<Competition> list(int page, int size, String status, String search);
    Competition getById(Long id, boolean increaseView);
}