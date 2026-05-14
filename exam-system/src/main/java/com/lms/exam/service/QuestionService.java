// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/service/QuestionService.java
// 설명: 기존 QuestionService.java에 아래 메서드들을 추가하세요
// ============================================

package com.lms.exam.service;

import com.lms.exam.dto.request.*;
import com.lms.exam.dto.response.PageResponse;
import com.lms.exam.exception.ResourceNotFoundException;
import com.lms.exam.mapper.*;
import com.lms.exam.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final QuestionMapper questionMapper;
    private final OptionMapper optionMapper;
    private final QuestionStatisticsMapper questionStatisticsMapper;

    // ============================================
    // ✅ NEW: 문제 수정 메서드 (아래 메서드들을 기존 파일에 추가)
    // ============================================

    /**
     * 문제 정보 수정 (문제 내용, 해설, 배점, 난이도)
     * @param questionId 문제 ID
     * @param request 수정 요청
     * @return 수정된 문제 정보
     */
    @Transactional
    public Question updateQuestion(Long questionId, UpdateQuestionRequest request) {
        Question question = questionMapper.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
        
        // 기본 정보 수정
        if (request.getQuestionText() != null) {
            question.setQuestionText(request.getQuestionText());
        }
        if (request.getExplanation() != null) {
            question.setExplanation(request.getExplanation());
        }
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        }
        if (request.getDifficulty() != null) {
            question.setDifficulty(request.getDifficulty());
        }
        
        questionMapper.update(question);
        
        // 선택지 수정
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            updateOptions(questionId, request.getOptions());
        }
        
        return questionMapper.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
    }

    /**
     * 선택지 수정
     * @param questionId 문제 ID
     * @param optionRequests 선택지 수정 요청 목록
     */
    @Transactional
    public void updateOptions(Long questionId, List<OptionUpdateRequest> optionRequests) {
        for (OptionUpdateRequest optionRequest : optionRequests) {
            if (optionRequest.getOptionId() != null) {
                // 기존 선택지 수정
                Option option = optionMapper.findById(optionRequest.getOptionId())
                        .orElseThrow(() -> new ResourceNotFoundException("선택지를 찾을 수 없습니다"));
                
                if (optionRequest.getOptionText() != null) {
                    option.setOptionText(optionRequest.getOptionText());
                }
                if (optionRequest.getIsCorrect() != null) {
                    option.setIsCorrect(optionRequest.getIsCorrect());
                }
                
                optionMapper.update(option);
            } else {
                // 새 선택지 추가
                Option newOption = Option.builder()
                        .questionId(questionId)
                        .optionText(optionRequest.getOptionText())
                        .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                        .build();
                optionMapper.insert(newOption);
            }
        }
    }

    /**
     * 선택지 삭제
     * @param optionId 선택지 ID
     */
    @Transactional
    public void deleteOption(Long optionId) {
        optionMapper.delete(optionId);
    }

    // ============================================
    // 기존 메서드들 (참고용)
    // ============================================

    @Transactional
    public Question createQuestion(CreateQuestionRequest request, Long userId) {
    	log.debug("=====QuestionService=====userId=={}===========", userId);
    	Question question = Question.builder()
                .categoryId(request.getCategoryId())
                .questionText(request.getQuestionText())
                .explanation(request.getExplanation())
                .points(request.getPoints() != null ? request.getPoints() : 5)
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "MEDIUM")
                .createdBy(userId)
                .build();
                
        questionMapper.insert(question);
        log.debug("=====QuestionService=====userId=={}===========", question);
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (CreateQuestionRequest.OptionRequest optionReq : request.getOptions()) {
            	log.debug("=====option==1={}", optionReq);
            	Option option = Option.builder()
                        .questionId(question.getQuestionId())
                        .optionOrder(optionReq.getOptionOrder())
                        .optionText(optionReq.getOptionText())
                        .isCorrect(optionReq.getIsCorrect())
                        .build();
                optionMapper.insert(option);
            }
        }
        log.debug("=====QuestionService=====option=============");
        QuestionStatistics stats = QuestionStatistics.builder()
                .questionId(question.getQuestionId())
                .totalAttempts(0)
                .correctCount(0)
                .wrongCount(0)
                .correctRate(0.0)
                .build();
        log.debug("=====question.getQuestionId()=====stats=={}===========",question.getQuestionId());
        questionStatisticsMapper.insert(stats);
        log.debug("=====QuestionService=====stats=={}===========",stats);
        return questionMapper.findById(question.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
    }

    public PageResponse<Question> getQuestions(PageRequest pageRequest) {
        List<Question> questions = questionMapper.findAll(pageRequest);
        Long total = questionMapper.count();
        return PageResponse.of(questions, pageRequest, total);
    }

    public Question getQuestionById(Long questionId) {
        return questionMapper.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
    }

    public List<Question> getQuestionsByCategory(Long categoryId, PageRequest pageRequest) {
        return questionMapper.findByCategoryId(categoryId, pageRequest);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        questionMapper.delete(questionId);
    }
}
