package com.lms.exam.controller;

import com.lms.exam.dto.request.*;
import com.lms.exam.dto.response.*;
import com.lms.exam.model.User;
import com.lms.exam.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser() {
        return ApiResponse.success(authService.getCurrentUser());
    }
    
    // ✅ NEW: 프로필 수정
    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = authService.getCurrentUser();
        return ApiResponse.success(authService.updateProfile(currentUser.getUserId(), request));
    }
}
