package com.lms.exam.controller;

import com.lms.exam.dto.request.SubmitAnswerRequest;
import com.lms.exam.dto.response.ApiResponse;
import com.lms.exam.model.UserExam;
import com.lms.exam.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-exams")
@RequiredArgsConstructor
@Tag(name = "시험 응시")
public class UserExamController {
    private final UserExamService userExamService;
    private final AuthService authService;

    @PostMapping("/start/{examId}")
    public ApiResponse<UserExam> startExam(@PathVariable Long examId) {
        Long userId = authService.getCurrentUser().getUserId();
        return ApiResponse.success(userExamService.startExam(userId, examId));
    }

    @PostMapping("/submit")
    public ApiResponse<UserExam> submitAnswers(@Valid @RequestBody SubmitAnswerRequest request) {
        return ApiResponse.success(userExamService.submitAnswers(request));
    }

    @GetMapping("/{userExamId}/result")
    public ApiResponse<UserExam> getUserExamResult(@PathVariable Long userExamId) {
        Long userId = authService.getCurrentUser().getUserId();
        return ApiResponse.success(userExamService.getUserExamResult(userId, userExamId));
    }
    
    @GetMapping("/my-exams")
    public ApiResponse<List<UserExam>> getMyExams() {
        Long userId = authService.getCurrentUser().getUserId();
        return ApiResponse.success(userExamService.getMyExams(userId));
    }
}
