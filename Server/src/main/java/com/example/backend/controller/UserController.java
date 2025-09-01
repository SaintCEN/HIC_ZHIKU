package com.example.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.UserDtos;
import com.example.backend.entity.User;
import com.example.backend.service.SecurityAuditService;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SecurityAuditService securityAuditService;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        String username = authentication.getName();
        User u = userService.getByUsername(username);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "data", userService.toProfile(u)));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(Authentication authentication, @RequestBody @Valid UserDtos.UpdateProfileReq req) {
        String username = authentication.getName();
        UserDtos.ProfileResp resp = userService.updateProfile(username, req);
        return ResponseEntity.ok(Map.of("code", 0, "message", "OK", "data", resp));
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody @Valid UserDtos.ChangePasswordReq req, HttpServletRequest request) {
        String username = authentication.getName();
        try {
            userService.changePassword(username, req);
            securityAuditService.logPasswordChange(username, request);
            return ResponseEntity.ok(Map.of("code", 0, "message", "OK"));
        } catch (Exception e) {
            securityAuditService.logSuspiciousActivity("PASSWORD_CHANGE_FAILURE", 
                "Password change failed for user: " + username, request);
            throw e;
        }
    }
}


