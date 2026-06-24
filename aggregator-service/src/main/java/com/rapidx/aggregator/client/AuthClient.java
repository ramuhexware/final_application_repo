package com.rapidx.aggregator.client;

import com.rapidx.aggregator.dto.LoginRequestDto;
import com.rapidx.aggregator.dto.LoginResponseDto;
import com.rapidx.aggregator.dto.RegisterRequestDto;
import com.rapidx.aggregator.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(WebClient.Builder webClientBuilder, @Value("${auth-service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    public Mono<LoginResponseDto> login(LoginRequestDto loginRequest) {
        return this.webClient.post()
                .uri("/api/auth/login")
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(LoginResponseDto.class);
    }

    public Mono<UserDto> register(RegisterRequestDto registerRequest) {
        return this.webClient.post()
                .uri("/api/auth/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<UserDto> validateToken(String tokenHeader) {
        return this.webClient.get()
                .uri("/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, tokenHeader)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
