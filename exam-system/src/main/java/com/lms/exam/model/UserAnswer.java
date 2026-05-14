package com.lms.exam.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {
    private Long answerId;
    private Long userExamId;
    private Long questionId;
    private Long selectedOptionId;
    private String answerText;
    private Boolean isCorrect;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String questionText;
    private String selectedOptionText;
    private String correctOptionText;
}
