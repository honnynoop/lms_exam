package com.lms.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateExamRequest {
    @NotBlank(message = "시험 제목은 필수입니다")
    private String examTitle;
    
    @NotNull(message = "카테고리는 필수입니다")
    private Long categoryId;
    
    private String description;
    
    @NotEmpty(message = "최소 1개 이상의 문제를 선택해야 합니다")
    private List<Long> questionIds;
    
    private Integer durationMinutes;
    private Integer passingScore;
    private LocalDate examDate;  // ✅ NEW
}
