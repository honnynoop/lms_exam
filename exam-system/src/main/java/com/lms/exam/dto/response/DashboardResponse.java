package com.lms.exam.dto.response;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalUsers;
    private Long totalCategories;
    private Long totalQuestions;
    private Long totalExams;
}
