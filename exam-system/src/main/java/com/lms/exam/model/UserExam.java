package com.lms.exam.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExam {
    private Long userExamId;
    private Long userId;
    private Long examId;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String fullName;
    private String examTitle;
    private String categoryName;
    private Boolean isPassed;
    
    private Integer isPublished;
    private List<UserAnswer> answers;
}
