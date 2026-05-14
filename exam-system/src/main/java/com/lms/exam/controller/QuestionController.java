// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/controller/QuestionController.java
// 설명: 기존 QuestionController.java에 아래 API들을 추가하세요
// ============================================

package com.lms.exam.controller;

import com.lms.exam.dto.request.*;
import com.lms.exam.dto.response.*;
import com.lms.exam.model.Question;
import com.lms.exam.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "문제 관리")
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    private final AuthService authService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 생성")
    public ApiResponse<Question> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        Long userId = authService.getCurrentUser().getUserId();
        log.debug("=====createQuestion=====userId=={}===========", userId);
        log.debug("=====createQuestion=====request=={}===========", request);
        return ApiResponse.success(questionService.createQuestion(request, userId));
    }

    @GetMapping
    @Operation(summary = "문제 목록 조회")
    public ApiResponse<PageResponse<Question>> getQuestions(PageRequest pageRequest) {
        return ApiResponse.success(questionService.getQuestions(pageRequest));
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "문제 상세 조회")
    public ApiResponse<Question> getQuestionById(@PathVariable Long questionId) {
        return ApiResponse.success(questionService.getQuestionById(questionId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 문제 조회")
    public ApiResponse<List<Question>> getQuestionsByCategory(
            @PathVariable Long categoryId,
            PageRequest pageRequest) {
        return ApiResponse.success(questionService.getQuestionsByCategory(categoryId, pageRequest));
    }

    // ============================================
    // ✅ NEW: 문제 수정 API (아래 메서드들을 기존 파일에 추가)
    // ============================================

    /**
     * 문제 정보 수정 (문제 내용, 해설, 배점, 난이도, 선택지)
     */
    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 수정", description = "문제의 내용, 해설, 배점, 난이도, 선택지를 수정합니다")
    public ApiResponse<Question> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request) {
        return ApiResponse.success(questionService.updateQuestion(questionId, request));
    }

    /**
     * 선택지 삭제
     */
    @DeleteMapping("/options/{optionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "선택지 삭제", description = "특정 선택지를 삭제합니다")
    public ApiResponse<Void> deleteOption(@PathVariable Long optionId) {
        questionService.deleteOption(optionId);
        return ApiResponse.success("선택지가 삭제되었습니다", null);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "문제 삭제")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ApiResponse.success("문제가 삭제되었습니다", null);
    }
}
