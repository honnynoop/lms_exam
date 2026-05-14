package com.lms.exam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatistics {
    private Long statId;
    private Long questionId;
    private Integer totalAttempts;
    private Integer correctCount;
    private Integer wrongCount;
    private Double correctRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // JOIN 데이터
    private String questionText;
    private String categoryName;
    private String difficulty;
}
