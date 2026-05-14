// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/mapper/OptionMapper.java
// 설명: 선택지 Mapper 인터페이스 (기존 파일 확인 후 없는 메서드 추가)
// ============================================

package com.lms.exam.mapper;

import com.lms.exam.model.Option;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OptionMapper {
    // 조회
    Optional<Option> findById(Long optionId);
    List<Option> findByQuestionId(Long questionId);
    
    // 생성/수정/삭제
    void insert(Option option);
    void update(Option option);
    void delete(Long optionId);
    void deleteByQuestionId(Long questionId);
}
