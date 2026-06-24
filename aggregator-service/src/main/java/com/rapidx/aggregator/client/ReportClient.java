package com.rapidx.aggregator.client;

import com.rapidx.aggregator.dto.ReportRecordDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class ReportClient {

    private final WebClient webClient;

    public ReportClient(WebClient.Builder webClientBuilder, @Value("${report-service.url}") String reportServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(reportServiceUrl).build();
    }

    public Mono<ReportRecordDto> createReport(ReportRecordDto reportDto) {
        return this.webClient.post()
                .uri("/api/reports")
                .bodyValue(reportDto)
                .retrieve()
                .bodyToMono(ReportRecordDto.class);
    }

    public Mono<List<ReportRecordDto>> getAllReports() {
        return this.webClient.get()
                .uri("/api/reports")
                .retrieve()
                .bodyToFlux(ReportRecordDto.class)
                .collectList();
    }
}
