// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/dto/request/UpdateQuestionRequest.java
// 설명: 문제 수정 요청 DTO
// ============================================

package com.lms.exam.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class UpdateQuestionRequest {
    private String questionText;        // 문제 내용
    private String explanation;         // 해설
    private Integer points;             // 배점
    private String difficulty;          // 난이도 (EASY, MEDIUM, HARD)
    private List<OptionUpdateRequest> options;  // 선택지 수정
}
