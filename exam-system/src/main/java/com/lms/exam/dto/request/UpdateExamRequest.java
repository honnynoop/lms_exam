// 위치: backend/src/main/java/com/lms/exam/dto/request/UpdateExamRequest.java
// 신규 파일 생성

package com.lms.exam.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateExamRequest {
    private String examTitle;
    private String description;
    private Integer durationMinutes;
    private Integer passingScore;
    private LocalDate examDate;
}
