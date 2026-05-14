package com.lms.exam.service;

import com.lms.exam.dto.request.LoginRequest;
import com.lms.exam.dto.request.RegisterRequest;
import com.lms.exam.dto.request.UpdateProfileRequest;
import com.lms.exam.dto.response.LoginResponse;
import com.lms.exam.exception.BusinessException;
import com.lms.exam.exception.ResourceNotFoundException;
import com.lms.exam.mapper.UserMapper;
import com.lms.exam.model.User;
import com.lms.exam.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        User user = userMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        return LoginResponse.builder()
                .accessToken(token)
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException("이미 존재하는 사용자명입니다");
        }
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException("이미 존재하는 이메일입니다");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role("USER")
                .isActive(true)
                .build();
        userMapper.insert(user);
        return user;
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
    }
    
    // ✅ NEW: 프로필 수정
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userMapper.existsByEmail(request.getEmail())) {
                throw new BusinessException("이미 존재하는 이메일입니다");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("현재 비밀번호가 일치하지 않습니다");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        userMapper.update(user);
        return userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
    }
}
