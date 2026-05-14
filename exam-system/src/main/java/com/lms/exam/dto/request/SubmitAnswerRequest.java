package com.lms.exam.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SubmitAnswerRequest {
    @NotNull
    private Long userExamId;
    @NotNull
    private List<AnswerItem> answers;
    
    @Data
    public static class AnswerItem {
        @NotNull
        private Long questionId;
        private Long selectedOptionId;
        private String answerText;
    }
}
