package com.lms.exam.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private String phone;           // ✅ NEW
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // ✅ NEW
}
