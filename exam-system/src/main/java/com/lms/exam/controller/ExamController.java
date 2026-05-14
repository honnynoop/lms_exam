// 위치: backend/src/main/java/com/lms/exam/controller/ExamController.java
// 기존 ExamController.java에 아래 메서드들을 추가하세요

package com.lms.exam.controller;

import com.lms.exam.dto.request.*;
import com.lms.exam.dto.response.*;
import com.lms.exam.model.Exam;
import com.lms.exam.model.Question;
import com.lms.exam.model.QuestionStatistics;
import com.lms.exam.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@Tag(name = "시험")
public class ExamController {
    private final ExamService examService;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Exam> createExam(@Valid @RequestBody CreateExamRequest request) {
        Long userId = authService.getCurrentUser().getUserId();
        return ApiResponse.success(examService.createExam(request, userId));
    }

    @GetMapping
    public ApiResponse<PageResponse<Exam>> getExams(PageRequest pageRequest) {
        return ApiResponse.success(examService.getExams(pageRequest));
    }
    
    @GetMapping("/active")
    public ApiResponse<List<Exam>> getActiveExams() {
        return ApiResponse.success(examService.getActiveExams());
    }

    @GetMapping("/{examId}")
    public ApiResponse<Exam> getExamById(@PathVariable Long examId) {
        return ApiResponse.success(examService.getExamById(examId));
    }
    
    @GetMapping("/{examId}/questions")
    public ApiResponse<List<Question>> getExamQuestions(@PathVariable Long examId) {
        return ApiResponse.success(examService.getExamQuestions(examId));
    }
    
    @GetMapping("/{examId}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<QuestionStatistics>> getExamStatistics(@PathVariable Long examId) {
        return ApiResponse.success(examService.getExamStatistics(examId));
    }

    // ✅ NEW: 시험 정보 수정 (공개 전에만 가능)
    @PutMapping("/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "시험 정보 수정", description = "시험 공개 전에만 수정 가능합니다")
    public ApiResponse<Exam> updateExam(
            @PathVariable Long examId,
            @RequestBody UpdateExamRequest request) {
        return ApiResponse.success(examService.updateExam(examId, request));
    }

    // ✅ NEW: 시험 문제 수정 (점수, 순서 등)
    @PutMapping("/{examId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "시험 문제 수정", description = "문제별 점수, 순서를 수정합니다")
    public ApiResponse<Void> updateExamQuestions(
            @PathVariable Long examId,
            @RequestBody List<QuestionUpdateRequest> questionUpdates) {
        examService.updateExamQuestions(examId, questionUpdates);
        return ApiResponse.success("시험 문제가 수정되었습니다", null);
    }

    @PutMapping("/{examId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> activateExam(@PathVariable Long examId) {
        examService.activateExam(examId);
        return ApiResponse.success("시험이 공개되었습니다", null);
    }
    
    @PutMapping("/{examId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deactivateExam(@PathVariable Long examId) {
        examService.deactivateExam(examId);
        return ApiResponse.success("시험이 비공개되었습니다", null);
    }

    @PutMapping("/{examId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> publishExamResults(@PathVariable Long examId) {
        examService.publishExamResults(examId);
        return ApiResponse.success("결과가 공개되었습니다", null);
    }
    
    @DeleteMapping("/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
        return ApiResponse.success("시험이 삭제되었습니다", null);
    }
    
}
