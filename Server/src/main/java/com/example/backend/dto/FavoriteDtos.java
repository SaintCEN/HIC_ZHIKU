package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteDtos {
    
    @Data
    public static class AddFavoriteReq {
        @NotNull
        private Long resourceId;
        @NotBlank
        private String resourceTitle;
        private String resourceUrl;
    }

    @Data
    public static class FavoriteResp {
        private Long id;
        private Long resourceId;
        private String resourceTitle;
        private String resourceUrl;
        private LocalDateTime createdAt;
        
        // 项目详细信息
        private String resourceDescription;
        private String authorName;
        private String authorAvatar;
        private Integer viewCount;
        private Integer starCount;
        private String category;
        private String githubUrl;
        private String demoUrl;
    }

    @Data
    public static class FavoriteListResp {
        private java.util.List<FavoriteResp> favorites;
        private Long total;
    }
}