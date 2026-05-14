// 위치: backend/src/main/java/com/lms/exam/dto/request/QuestionUpdateRequest.java
// 신규 파일 생성

package com.lms.exam.dto.request;

import lombok.Data;

@Data
public class QuestionUpdateRequest {
    private Long questionId;
    private Integer points;
    private Integer questionOrder;
}
