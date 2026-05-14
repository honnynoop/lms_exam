package com.lms.exam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class CorsConfig {

	@Value("${cors.allowed-origins}") 
	private List<String> allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
    	for (String urls: allowedOrigins) {
    		log.debug("CorsConfig  -------------------------  {}", urls);
		}

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);  // properties에서 읽어옮
        /*
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://172.30.1.36:5173",
                "http://172.30.1.36:8080",
                "http://localhost:8080"
            ));
            */
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
    
   
}
