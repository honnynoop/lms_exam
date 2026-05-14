// 위치: backend/src/main/java/com/lms/exam/service/UserService.java

package com.lms.exam.service;

import com.lms.exam.exception.ResourceNotFoundException;
import com.lms.exam.mapper.UserMapper;
import com.lms.exam.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;

    /**
     * 전체 사용자 목록 조회
     * @return 전체 사용자 리스트
     */
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAll();
        // 비밀번호 필드 제거 (보안)
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    /**
     * 사용자 ID로 조회
     * @param userId 사용자 ID
     * @return 사용자 정보 (비밀번호 제외)
     */
    public User getUserById(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        // 비밀번호 제거 (보안)
        user.setPassword(null);
        return user;
    }

    /**
     * 사용자명으로 조회
     * @param username 사용자명
     * @return 사용자 정보 (비밀번호 제외)
     */
    public User getUserByUsername(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        // 비밀번호 제거 (보안)
        user.setPassword(null);
        return user;
    }

    /**
     * 전체 사용자 수 조회
     * @return 사용자 수
     */
    public Long getUserCount() {
        return userMapper.count();
    }

    /**
     * 사용자 활성/비활성 상태 변경
     * @param userId 사용자 ID
     * @param isActive 활성 상태
     */
    @Transactional
    public void updateUserStatus(Long userId, Boolean isActive) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        user.setIsActive(isActive);
        userMapper.update(user);
    }

    /**
     * 사용자 삭제
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));
        
        // 관리자는 삭제 불가
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다");
        }
        
        userMapper.delete(userId);
    }
}
