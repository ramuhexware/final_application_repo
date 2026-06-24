package com.rapidx.aggregator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Aggregator and Gateway Service API")
                        .version("1.0.0")
                        .description("Microservice orchestrating calls to Auth Service and Account Service."));
    }
}
