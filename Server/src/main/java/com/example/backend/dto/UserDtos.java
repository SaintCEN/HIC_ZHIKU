package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDtos {
    @Data
    public static class ProfileResp {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String avatarUrl;
        private String college;
        private String bio;
        private Integer status;
        private Integer hic;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
    }

    @Data
    public static class UpdateProfileReq {
        private String nickname;
        private String email;
        private String avatarUrl;
        private String college;
        private String bio;
        private Integer hic;
    }

    @Data
    public static class ChangePasswordReq {
        @NotBlank(message = "原密码不能为空")
        @Size(min = 1, max = 128, message = "原密码长度不能超过128个字符")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, max = 128, message = "新密码长度必须在8-128个字符之间")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
                message = "新密码必须包含至少一个小写字母、一个大写字母、一个数字和一个特殊字符")
        private String newPassword;
    }
}


