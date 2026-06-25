package com.rapidx.aggregator.service;

import com.rapidx.aggregator.client.OAuthTokenCache;
import com.rapidx.aggregator.entity.OimSyncError;
import com.rapidx.aggregator.exception.UcsApiException;
import com.rapidx.aggregator.repository.OimSyncErrorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);

    private final WebClient webClient;
    private final OAuthTokenCache oAuthTokenCache;
    private final OimSyncErrorRepository oimSyncErrorRepository;

    @Value("${oim.sync.bypass:false}")
    private boolean byPassOimSync;

    @Value("${api.retries.max:3}")
    private long apiRetriesMax;

    @Value("${api.retries.delay:2}")
    private long apiRetriesDelay;

    public ApiServiceImpl(WebClient.Builder webClientBuilder, OAuthTokenCache oAuthTokenCache, OimSyncErrorRepository oimSyncErrorRepository) {
        this.webClient = webClientBuilder.build();
        this.oAuthTokenCache = oAuthTokenCache;
        this.oimSyncErrorRepository = oimSyncErrorRepository;
    }

    @Override
    @Async("oimDataSyncThreadpool")
    public <T> void get(String url, Object object, String operation, Class<T> responseType) {
        if (!byPassOimSync) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread Interrupted exception " + e);
            }
            LOGGER.info("ORG API: OIM Sync Get service call started... for URL-" + url + " Primary Key-" + object + " Opreation-" + operation + " Thread name " + Thread.currentThread().getName());

            webClient.get()
                    .uri(url)
                    .headers(h -> h.add("Authorization", oAuthTokenCache.getOAuthAccessToken()))
                    .exchangeToMono(clientResponse -> {
                        LOGGER.info("Recived response from OIM Sync application with status code " + clientResponse.statusCode().value());
                        if (clientResponse.statusCode().is4xxClientError()) {
                            throw new UcsApiException(HttpStatus.valueOf(clientResponse.statusCode().value()), "Org API :: URL is wrong ");
                        } else if (clientResponse.statusCode().is5xxServerError()) {
                            throw new UcsApiException(HttpStatus.valueOf(clientResponse.statusCode().value()), " Org API :: Error occured in OIM Sync :: for more details check OIM sync logs");
                        } else {
                            return clientResponse.bodyToMono(responseType);
                        }
                    })
                    .retryWhen(Retry.backoff(apiRetriesMax, Duration.ofSeconds(apiRetriesDelay))
                            .jitter(0d)
                            .doAfterRetry(retrySignal -> LOGGER.info("Retried " + retrySignal.totalRetries()))
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new UcsApiException(HttpStatus.valueOf(500),
                                    " Retries exhausted ", retrySignal.failure())))
                    .doOnSuccess(clientResponse -> LOGGER.info("Received reply from " + url + " Primary Key-" + object + " Opreation-" + operation + " Thread name " + Thread.currentThread().getName() + " with response " + clientResponse))
                    .doOnError(Throwable.class, (msg) -> {
                        saveToErrorTable(url, object.toString(), operation, msg);
                        LOGGER.error("Exception while calling get for " + url + " for entity " + object.toString() + " while " + operation + " Exception msg " + msg + " Thread name " + Thread.currentThread().getName());
                    })
                    .subscribe();
        }
    }

    private void saveToErrorTable(String url, String entityId, String operation, Throwable msg) {
        try {
            OimSyncError error = OimSyncError.builder()
                    .url(url)
                    .entityId(entityId)
                    .operation(operation)
                    .errorMessage(msg != null ? msg.getMessage() : "Unknown error")
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            oimSyncErrorRepository.save(error);
            LOGGER.info("Error successfully persisted to OIM sync error table.");
        } catch (Exception e) {
            LOGGER.error("Failed to save error to table: {}", e.getMessage(), e);
        }
    }
}
