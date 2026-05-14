package com.lms.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.lms.exam.mapper")
public class LmsExamSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LmsExamSystemApplication.class, args);
        System.out.println("\n✅ LMS 시험 관리 시스템 시작 완료!");
        System.out.println("Swagger: http://localhost:8080/swagger-ui.html\n");
    }
}
