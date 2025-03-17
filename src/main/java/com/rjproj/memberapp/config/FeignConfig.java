package com.rjproj.memberapp.config;

import com.rjproj.memberapp.security.JWTUtil;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Autowired
    JWTUtil jwtUtil;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Fetch the token from the context (e.g., SecurityContext, thread-local storage, etc.)
            String token = "Bearer " + jwtUtil.getToken(); // replace with your logic to get token

            // Add the Authorization header to every request
            requestTemplate.header("Authorization", token);
        };
    }
}