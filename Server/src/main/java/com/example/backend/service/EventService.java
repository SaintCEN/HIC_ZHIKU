package com.example.backend.service;

import com.example.backend.entity.Event;

import java.util.List;

public interface EventService {
    List<Event> listAll(int page, int size, String type, String search);
    long count(String type, String search);
    List<Event> latest(int limit);
    Event getById(Long id, boolean increaseView);
}