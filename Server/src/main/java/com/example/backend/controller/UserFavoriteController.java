package com.example.backend.controller;

import com.example.backend.dto.FavoriteDtos;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.UserFavorite;
import com.example.backend.service.UserFavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {
    
    private final UserFavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<?> addFavorite(Authentication authentication, @RequestBody @Valid FavoriteDtos.AddFavoriteReq req) {
        String username = authentication.getName();
        favoriteService.addFavorite(username, req.getResourceId(), req.getResourceTitle(), req.getResourceUrl());
        return ResponseEntity.ok(Map.of("code", 0, "message", "收藏成功"));
    }

    @DeleteMapping
    public ResponseEntity<?> removeFavorite(Authentication authentication, 
                                          @RequestParam Long resourceId) {
        String username = authentication.getName();
        favoriteService.removeFavorite(username, resourceId);
        return ResponseEntity.ok(Map.of("code", 0, "message", "取消收藏成功"));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<?> removeFavoriteById(Authentication authentication, @PathVariable Long favoriteId) {
        String username = authentication.getName();
        favoriteService.removeFavoriteById(username, favoriteId);
        return ResponseEntity.ok(Map.of("code", 0, "message", "删除收藏成功"));
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(Authentication authentication,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("code", 401, "message", "未认证"));
        }
        
        String username = authentication.getName();
        PageResponse<FavoriteDtos.FavoriteResp> favorites = favoriteService.getFavorites(username, page, size);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "data", favorites));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkFavorite(Authentication authentication,
                                         @RequestParam Long resourceId) {
        String username = authentication.getName();
        boolean isFavorited = favoriteService.isFavorited(username, resourceId);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "data", Map.of("isFavorited", isFavorited)));
    }
}