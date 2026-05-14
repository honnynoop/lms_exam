// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/service/ExamService.java
// 설명: 시험 Service - getExamQuestions 메서드 수정
// 기존 ExamService.java에서 getExamQuestions 메서드를 아래로 교체하세요
// ============================================

package com.lms.exam.service;

import com.lms.exam.dto.request.*;
import com.lms.exam.dto.response.PageResponse;
import com.lms.exam.exception.ResourceNotFoundException;
import com.lms.exam.mapper.*;
import com.lms.exam.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamMapper examMapper;
    private final QuestionMapper questionMapper;
    private final QuestionStatisticsMapper questionStatisticsMapper;


    // ============================================
    // ✅ FIXED: 시험 문제 조회 (options 포함)
    // ============================================
    
    /**
     * 시험의 문제 목록 조회 (선택지 포함)
     * @param examId 시험 ID
     * @return 문제 목록 (options 포함)
     */
    public List<Question> getExamQuestions(Long examId) {
        // ✅ 수정: examMapper에서 직접 questions + options 조회
        return examMapper.findQuestionsByExamId(examId);
    }

    // ============================================
    // 기존 메서드들 (참고용)
    // ============================================

    @Transactional
    public Exam createExam(CreateExamRequest request, Long userId) {
        int totalPoints = 0;
        for (Long questionId : request.getQuestionIds()) {
            Question question = questionMapper.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
            totalPoints += question.getPoints();
        }
        
        Exam exam = Exam.builder()
                .examTitle(request.getExamTitle())
                .categoryId(request.getCategoryId())
                .description(request.getDescription())
                .totalQuestions(request.getQuestionIds().size())
                .totalPoints(totalPoints)
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .passingScore(request.getPassingScore() != null ? request.getPassingScore() : 60)
                .examDate(request.getExamDate())
                .isPublished(false)
                .isActive(false)
                .createdBy(userId)
                .build();
                
        examMapper.insert(exam);
        
        for (int i = 0; i < request.getQuestionIds().size(); i++) {
            examMapper.insertExamQuestion(exam.getExamId(), request.getQuestionIds().get(i), i + 1);
        }
        
        return examMapper.findById(exam.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("시험을 찾을 수 없습니다"));
    }

    public PageResponse<Exam> getExams(PageRequest pageRequest) {
        List<Exam> exams = examMapper.findAll(pageRequest);
        Long total = examMapper.count();
        return PageResponse.of(exams, pageRequest, total);
    }
    
    public List<Exam> getActiveExams() {
        return examMapper.findActiveExams();
    }

    public Exam getExamById(Long examId) {
        return examMapper.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("시험을 찾을 수 없습니다"));
    }

    @Transactional
    public Exam updateExam(Long examId, UpdateExamRequest request) {
        Exam exam = getExamById(examId);
        
        if (exam.getIsActive()) {
            throw new IllegalStateException("이미 공개된 시험은 수정할 수 없습니다");
        }
        
        if (request.getExamTitle() != null) {
            exam.setExamTitle(request.getExamTitle());
        }
        if (request.getDescription() != null) {
            exam.setDescription(request.getDescription());
        }
        if (request.getDurationMinutes() != null) {
            exam.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getPassingScore() != null) {
            exam.setPassingScore(request.getPassingScore());
        }
        if (request.getExamDate() != null) {
            exam.setExamDate(request.getExamDate());
        }
        
        examMapper.update(exam);
        
        return getExamById(examId);
    }

    @Transactional
    public void updateExamQuestions(Long examId, List<QuestionUpdateRequest> questionUpdates) {
        Exam exam = getExamById(examId);
        
        if (exam.getIsActive()) {
            throw new IllegalStateException("이미 공개된 시험은 수정할 수 없습니다");
        }
        
        int totalPoints = 0;
        
        for (QuestionUpdateRequest update : questionUpdates) {
            Question question = questionMapper.findById(update.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
            
            if (update.getPoints() != null) {
                question.setPoints(update.getPoints());
                questionMapper.update(question);
            }
            
            totalPoints += question.getPoints();
            
            if (update.getQuestionOrder() != null) {
                examMapper.updateExamQuestionOrder(examId, update.getQuestionId(), update.getQuestionOrder());
            }
        }
        
        exam.setTotalPoints(totalPoints);
        examMapper.update(exam);
    }
    
    @Transactional
    public void activateExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setIsActive(true);
        examMapper.update(exam);
    }
    
    @Transactional
    public void deactivateExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setIsActive(false);
        examMapper.update(exam);
    }

    @Transactional
    public void publishExamResults(Long examId) {
        Exam exam = getExamById(examId);
        exam.setIsPublished(true);
        examMapper.update(exam);
    }
    
    public List<QuestionStatistics> getExamStatistics(Long examId) {
        List<Long> questionIds = examMapper.findQuestionIdsByExamId(examId);
        return questionIds.stream()
                .map(questionId -> questionStatisticsMapper.findByQuestionId(questionId)
                        .orElse(QuestionStatistics.builder()
                                .questionId(questionId)
                                .totalAttempts(0)
                                .correctCount(0)
                                .wrongCount(0)
                                .build()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public void deleteExam(Long examId) {
        examMapper.delete(examId);
    }
}
