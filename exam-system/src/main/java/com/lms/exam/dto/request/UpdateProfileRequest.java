package com.lms.exam.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    private String phone;
    private String currentPassword;
    private String newPassword;
}
