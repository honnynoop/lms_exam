package com.lms.exam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6)
    private String password;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email
    private String email;
    
    @NotBlank(message = "이름은 필수입니다")
    private String fullName;
    
    private String phone;  // ✅ NEW
}
