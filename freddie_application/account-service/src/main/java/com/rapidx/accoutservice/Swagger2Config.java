package com.rapidx.accoutservice;

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
                        .title("User Counterparty Account Microservice API")
                        .version("1.0.0")
                        .description("Production-grade microservice for managing and filtering User Counterparty Accounts."));
    }
}
