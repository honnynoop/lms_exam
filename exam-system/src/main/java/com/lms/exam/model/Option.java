// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/model/Option.java
// 설명: 문제 선택지 모델 클래스
// ============================================

package com.lms.exam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    private Long optionId;          // 선택지 ID (Primary Key)
    private Long questionId;        // 문제 ID (Foreign Key)
    private String optionText;      // 선택지 내용
    private Boolean isCorrect;      // 정답 여부
    private Integer optionOrder;
}
