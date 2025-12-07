package com.clickbait.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://clickbait-frontend-web.vercel.app",
                            "http://localhost:8081",     // metro bundler
                            "http://10.0.2.2",           // android emulador
                            "http://10.0.2.2:8081",      // android metro
                            "http://localhost",          // fallback
                            "exp://*",                   // expo Go
                            "http://127.0.0.1",          // iOS simulator
                            "http://127.0.0.1:8081"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }
}
