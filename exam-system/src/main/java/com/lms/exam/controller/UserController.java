// 위치: backend/src/main/java/com/lms/exam/controller/UserController.java

package com.lms.exam.controller;

import com.lms.exam.dto.response.ApiResponse;
import com.lms.exam.model.User;
import com.lms.exam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "사용자 조회 및 관리 API")
public class UserController {
    private final UserService userService;

    /**
     * 전체 사용자 목록 조회
     * @return 전체 사용자 리스트 (비밀번호 제외)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 목록을 조회합니다 (관리자 전용)")
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    /**
     * 사용자 ID로 조회
     * @param userId 사용자 ID
     * @return 사용자 정보 (비밀번호 제외)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다 (관리자 전용)")
    public ApiResponse<User> getUserById(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }

    /**
     * 사용자명으로 조회
     * @param username 사용자명
     * @return 사용자 정보 (비밀번호 제외)
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자명으로 조회", description = "사용자명으로 사용자를 조회합니다 (관리자 전용)")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        return ApiResponse.success(userService.getUserByUsername(username));
    }

    /**
     * 전체 사용자 수 조회
     * @return 사용자 수
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 수 조회", description = "전체 사용자 수를 조회합니다 (관리자 전용)")
    public ApiResponse<Long> getUserCount() {
        return ApiResponse.success(userService.getUserCount());
    }

    /**
     * 사용자 활성/비활성 상태 변경
     * @param userId 사용자 ID
     * @param isActive 활성 상태
     * @return 성공 메시지
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 상태 변경", description = "사용자의 활성/비활성 상태를 변경합니다 (관리자 전용)")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive) {
        userService.updateUserStatus(userId, isActive);
        String message = isActive ? "사용자가 활성화되었습니다" : "사용자가 비활성화되었습니다";
        return ApiResponse.success(message, null);
    }

    /**
     * 사용자 삭제
     * @param userId 사용자 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다 (관리자 전용, 관리자 계정은 삭제 불가)")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success("사용자가 삭제되었습니다", null);
    }
}
