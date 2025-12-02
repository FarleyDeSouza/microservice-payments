package com.clickbait.payments.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("API for processing payments in the e-commerce system")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Clickbait E-commerce")
                                .email("support@clickbait.com")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server"));
    }
}