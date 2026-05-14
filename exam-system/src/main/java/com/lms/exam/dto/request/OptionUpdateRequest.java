// ============================================
// 파일 위치: backend/src/main/java/com/lms/exam/dto/request/OptionUpdateRequest.java
// 설명: 선택지 수정 요청 DTO
// ============================================

package com.lms.exam.dto.request;

import lombok.Data;

@Data
public class OptionUpdateRequest {
    private Long optionId;          // 선택지 ID (기존 선택지 수정 시)
    private String optionText;      // 선택지 내용
    private Boolean isCorrect;      // 정답 여부
}
