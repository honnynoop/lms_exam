package com.lms.exam.service;

import com.lms.exam.dto.request.*;
import com.lms.exam.exception.*;
import com.lms.exam.mapper.*;
import com.lms.exam.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserExamService {
    private final UserExamMapper userExamMapper;
    private final ExamMapper examMapper;
    private final QuestionMapper questionMapper;
    private final QuestionStatisticsMapper questionStatisticsMapper;

    @Transactional
    public UserExam startExam(Long userId, Long examId) {
        // ✅ 공개된 시험인지 확인
        Exam exam = examMapper.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("시험을 찾을 수 없습니다"));
        
        if (!exam.getIsActive()) {
            throw new BusinessException("공개되지 않은 시험입니다");
        }
        
        // 이미 제출한 시험인지 확인
        userExamMapper.findByUserIdAndExamId(userId, examId).ifPresent(ue -> {
            if ("SUBMITTED".equals(ue.getStatus())) {
                throw new BusinessException("이미 제출한 시험입니다");
            }
        });
        
        UserExam userExam = UserExam.builder()
                .userId(userId)
                .examId(examId)
                .startedAt(LocalDateTime.now())
                .totalQuestions(exam.getTotalQuestions())
                .status("IN_PROGRESS")
                .build();
        userExamMapper.insert(userExam);
        return userExam;
    }

    @Transactional
    public UserExam submitAnswers(SubmitAnswerRequest request) {
        UserExam userExam = userExamMapper.findById(request.getUserExamId())
                .orElseThrow(() -> new ResourceNotFoundException("시험 응시 기록을 찾을 수 없습니다"));
        
        int correctCount = 0;
        int totalScore = 0;
        
        for (SubmitAnswerRequest.AnswerItem answerItem : request.getAnswers()) {
            Question question = questionMapper.findById(answerItem.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("문제를 찾을 수 없습니다"));
            
            boolean isCorrect = false;
            
            if (answerItem.getSelectedOptionId() != null) {
                List<Option> options = questionMapper.findOptionsByQuestionId(answerItem.getQuestionId());
                Option correctOption = options.stream()
                        .filter(Option::getIsCorrect)
                        .findFirst()
                        .orElse(null);
                
                if (correctOption != null && correctOption.getOptionId().equals(answerItem.getSelectedOptionId())) {
                    isCorrect = true;
                    totalScore += question.getPoints();
                    correctCount++;
                }
            }
            
            UserAnswer answer = UserAnswer.builder()
                    .userExamId(request.getUserExamId())
                    .questionId(answerItem.getQuestionId())
                    .selectedOptionId(answerItem.getSelectedOptionId())
                    .answerText(answerItem.getAnswerText())
                    .isCorrect(isCorrect)
                    .build();
            userExamMapper.insertAnswer(answer);
            
            // ✅ 통계 업데이트
            questionStatisticsMapper.incrementAttempts(answerItem.getQuestionId());
            if (isCorrect) {
                questionStatisticsMapper.incrementCorrect(answerItem.getQuestionId());
            } else {
                questionStatisticsMapper.incrementWrong(answerItem.getQuestionId());
            }
        }
        
        userExam.setSubmittedAt(LocalDateTime.now());
        userExam.setScore(totalScore);
        userExam.setCorrectAnswers(correctCount);
        userExam.setWrongAnswers(userExam.getTotalQuestions() - correctCount);
        userExam.setStatus("SUBMITTED");
        userExamMapper.update(userExam);
        
        // ✅ 제출 직후 점수 숨김 (결과 공개 전)
        UserExam response = UserExam.builder()
                .userExamId(userExam.getUserExamId())
                .userId(userExam.getUserId())
                .examId(userExam.getExamId())
                .startedAt(userExam.getStartedAt())
                .submittedAt(userExam.getSubmittedAt())
                .totalQuestions(userExam.getTotalQuestions())
                .status("SUBMITTED")
                .build();
        
        return response;
    }

    public UserExam getUserExamResult(Long userId, Long userExamId) {
        UserExam userExam = userExamMapper.findById(userExamId)
                .orElseThrow(() -> new ResourceNotFoundException("시험 응시 기록을 찾을 수 없습니다"));
        
        if (!userExam.getUserId().equals(userId)) {
            throw new BusinessException("권한이 없습니다");
        }
        
        Exam exam = examMapper.findById(userExam.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("시험을 찾을 수 없습니다"));
        
        // ✅ 결과 공개 확인
        if (!exam.getIsPublished()) {
            throw new BusinessException("아직 결과가 공개되지 않았습니다");
        }
        
        List<UserAnswer> answers = userExamMapper.findAnswersByUserExamId(userExamId);
        userExam.setAnswers(answers);
        userExam.setExamTitle(exam.getExamTitle());
        userExam.setCategoryName(exam.getCategoryName());
        userExam.setScore(exam.getPassingScore());
        
        return userExam;
    }
    
    public List<UserExam> getMyExams(Long userId) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setSize(100);
        return userExamMapper.findByUserId(userId, pageRequest);
    }
}
