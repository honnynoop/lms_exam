// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/model/Question.java
// 설명: 문제 모델 클래스 (options 필드 포함)
// 기존 Question.java에 options 필드가 없다면 추가하세요
// ============================================

package com.lms.exam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long questionId;
    private Long categoryId;
    private String categoryName;        // JOIN으로 가져오는 필드
    private String questionText;
    private String explanation;
    private Integer points;
    private String difficulty;          // EASY, MEDIUM, HARD
    private Long createdBy;
    private LocalDateTime createdAt;
    
    // ✅ 중요: options 필드 (선택지 목록)
    private List<Option> options;       // 이 필드가 있어야 합니다!
}
