// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/mapper/ExamMapper.java
// 설명: 시험 Mapper 인터페이스 - findQuestionsByExamId 추가
// 기존 ExamMapper.java에 아래 메서드를 추가하세요
// ============================================

package com.lms.exam.mapper;

import com.lms.exam.dto.request.PageRequest;
import com.lms.exam.model.Exam;
import com.lms.exam.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ExamMapper {
    // 조회
    Optional<Exam> findById(Long examId);
    List<Exam> findAll(PageRequest pageRequest);
    List<Exam> findActiveExams();
    Long count();
    
    // 생성/수정/삭제
    void insert(Exam exam);
    void update(Exam exam);
    void delete(Long examId);
    
    // 시험-문제 관계
    void insertExamQuestion(@Param("examId") Long examId, 
                           @Param("questionId") Long questionId, 
                           @Param("questionOrder") Integer questionOrder);
    List<Long> findQuestionIdsByExamId(Long examId);
    void updateExamQuestionOrder(@Param("examId") Long examId, 
                                 @Param("questionId") Long questionId, 
                                 @Param("questionOrder") Integer questionOrder);
    
    // ✅ NEW: 시험의 문제 목록 조회 (options 포함)
    List<Question> findQuestionsByExamId(Long examId);
}
