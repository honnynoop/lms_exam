// 위치: backend/src/main/java/com/lms/exam/mapper/UserMapper.java
// 기존 파일이 있다면 아래 메서드들이 모두 있는지 확인하세요

package com.lms.exam.mapper;

import com.lms.exam.model.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    // 단일 조회
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    
    // 목록 조회
    List<User> findAll();  // ✅ userAPI.getAll()을 위한 메서드
    
    // 생성/수정/삭제
    void insert(User user);
    void update(User user);
    void delete(Long userId);
    
    // 중복 체크
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // 통계
    Long count();
}
