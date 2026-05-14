package com.lms.exam.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CreateQuestionRequest {
    @NotNull
    private Long categoryId;
    @NotBlank
    private String questionText;
    @NotBlank
    private String questionType;
    private String difficulty;
    @Positive
    private Integer points;
    private String explanation;
    private List<OptionRequest> options;
    
    @Data
    public static class OptionRequest {
        @NotBlank
        private String optionText;
        @NotNull
        private Integer optionOrder;
        private Boolean isCorrect;
    }
}
