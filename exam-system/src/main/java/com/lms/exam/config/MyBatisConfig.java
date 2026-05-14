package com.lms.exam.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.lms.exam.mapper")
public class MyBatisConfig {
}
