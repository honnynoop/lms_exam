package com.lms.exam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    private Long examId;
    private String examTitle;
    private Long categoryId;
    private String description;
    private Integer totalQuestions;
    private Integer totalPoints;
    private Integer durationMinutes;
    private Integer passingScore;
    private Boolean isPublished;    // 결과 공개 여부
    private Boolean isActive;       // 시험 공개 여부 ✅ NEW
    private LocalDate examDate;     // 시험 날짜 ✅ NEW
    private Long createdBy;
    private LocalDateTime createdAt;
    private String categoryName;    // JOIN
}
