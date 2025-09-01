package com.example.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.UserDtos;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public UserDtos.ProfileResp toProfile(User u) {
        UserDtos.ProfileResp p = new UserDtos.ProfileResp();
        p.setId(u.getId());
        p.setUsername(u.getUsername());
        p.setNickname(u.getNickname());
        p.setEmail(u.getEmail());
        p.setAvatarUrl(u.getAvatarUrl());
        p.setCollege(u.getCollege());
        p.setBio(u.getBio());
        p.setStatus(u.getStatus());
        p.setHic(u.getHic() != null ? u.getHic() : 0);
        p.setLastLoginTime(u.getLastLoginTime());
        p.setCreatedAt(u.getCreatedAt());
        return p;
    }

    public UserDtos.ProfileResp updateProfile(String username, UserDtos.UpdateProfileReq req) {
        User u = userMapper.findByUsername(username);
        if (u == null) throw new RuntimeException("用户不存在");
        User patch = new User();
        patch.setId(u.getId());
        patch.setNickname(req.getNickname());
        patch.setEmail(req.getEmail());
        patch.setAvatarUrl(req.getAvatarUrl());
        patch.setCollege(req.getCollege());
        patch.setBio(req.getBio());
        patch.setHic(req.getHic());
        userMapper.updateProfile(patch);
        return toProfile(userMapper.findById(u.getId()));
    }

    public void changePassword(String username, UserDtos.ChangePasswordReq req) {
        User u = userMapper.findByUsername(username);
        if (u == null) throw new RuntimeException("用户不存在");
        if (!passwordEncoder.matches(req.getOldPassword(), u.getPasswordHash())) {
            throw new RuntimeException("原密码不正确");
        }
        userMapper.updatePassword(u.getId(), passwordEncoder.encode(req.getNewPassword()));
    }
}


