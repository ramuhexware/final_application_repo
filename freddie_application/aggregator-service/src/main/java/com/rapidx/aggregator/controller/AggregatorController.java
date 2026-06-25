package com.rapidx.aggregator.controller;

import com.rapidx.aggregator.client.AuthClient;
import com.rapidx.aggregator.client.ReportClient;
import com.rapidx.aggregator.dto.*;
import java.util.List;
import com.rapidx.aggregator.entity.UserCntrPrtyAcct;
import com.rapidx.aggregator.processor.UserCntrPrtyAcctProcessor;
import com.rapidx.aggregator.repository.UserCntrPrtyAcctRepository;
import com.rapidx.aggregator.specification.UserCntrPrtyAcctSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import com.rapidx.aggregator.service.ApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/portal")
@Slf4j
@Tag(name = "Aggregator Portal Controller", description = "Aggregated gateway endpoints utilizing WebClient and specifications")
public class AggregatorController {

    private final AuthClient authClient;
    private final UserCntrPrtyAcctRepository repository;
    private final UserCntrPrtyAcctProcessor processor;
    private final ApiService apiService;
    private final RestTemplate restTemplate;
    private final ReportClient reportClient;
    private final String accountServiceUrl;

    public AggregatorController(
            AuthClient authClient,
            UserCntrPrtyAcctRepository repository,
            UserCntrPrtyAcctProcessor processor,
            ApiService apiService,
            RestTemplate restTemplate,
            ReportClient reportClient,
            @Value("${account-service.url}") String accountServiceUrl) {
        this.authClient = authClient;
        this.repository = repository;
        this.processor = processor;
        this.apiService = apiService;
        this.restTemplate = restTemplate;
        this.reportClient = reportClient;
        this.accountServiceUrl = accountServiceUrl;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user through auth-service client")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("Aggregator portal login request for username: {}", loginRequest.getUsername());
        LoginResponseDto response = authClient.login(loginRequest).block();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user through auth-service client")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto registerRequest) {
        log.info("Aggregator portal register request for username: {}", registerRequest.getUsername());
        UserDto response = authClient.register(registerRequest).block();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/accounts/filter")
    @Operation(summary = "Validate token, then dynamically filter local accounts using Specification and mapping via Processor")
    public ResponseEntity<Page<ActionAccountDto>> filterAccounts(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam(required = false) Integer orgId,
            @RequestParam(required = false) String roleStatus,
            @RequestParam(required = false) String lineOfBusiness,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("Aggregator portal filter accounts request with token validation. Org: {}, Status: {}, LOB: {}",
                orgId, roleStatus, lineOfBusiness);

        // 1. Validate token with auth-service (raises exception if invalid)
        UserDto user = authClient.validateToken(tokenHeader).block();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.debug("Token validation successful. User: {}, Roles: {}", user.getUsername(), user.getRoles());

        // 2. Perform dynamic query using JPA Specification
        Specification<UserCntrPrtyAcct> spec = UserCntrPrtyAcctSpecification.filterAccounts(orgId, roleStatus, lineOfBusiness);
        Page<UserCntrPrtyAcct> accountsPage = repository.findAll(spec, pageable);

        // 3. Transform to DTO via Processor mapping
        Page<ActionAccountDto> dtoPage = accountsPage.map(processor::transformToDto);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/accounts/update-family/{id}")
    @Operation(summary = "Validate token, then update account family name downstream using ApiService.get")
    public ResponseEntity<String> updateFamily(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable("id") Long id,
            @RequestParam("value") String value
    ) {
        log.info("Aggregator portal update family request for account ID: {} with value: {}", id, value);

        // 1. Validate token with auth-service (raises exception if invalid)
        UserDto user = authClient.validateToken(tokenHeader).block();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Build downstream url and trigger the async call
        String downstreamUrl = accountServiceUrl + "/accounts/update-family/" + id;
        apiService.get(downstreamUrl, id, value, String.class);

        return ResponseEntity.ok("OIM synchronization triggered asynchronously");
    }

    @GetMapping("/accounts/quick-search")
    @Operation(summary = "Validate token, then search organizations and counterparty accounts downstream")
    public ResponseEntity<String> quickSearch(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam("searchKey") String searchKey,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        log.info("Aggregator portal quick search request with key: {}", searchKey);

        // 1. Validate token with auth-service (raises exception if invalid)
        UserDto user = authClient.validateToken(tokenHeader).block();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Fetch downstream data using RestTemplate
        String url = accountServiceUrl + "/accounts/quick-search?searchKey=" + searchKey + "&page=" + page + "&size=" + size;
        String response = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports")
    @Operation(summary = "Validate token, then create report record in report-service")
    public ResponseEntity<ReportRecordDto> createReport(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ReportRecordDto reportDto
    ) {
        log.info("Aggregator portal create report request: {}", reportDto.getTitle());
        UserDto user = authClient.validateToken(tokenHeader).block();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReportRecordDto response = reportClient.createReport(reportDto).block();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/reports")
    @Operation(summary = "Validate token, then fetch all report records from report-service")
    public ResponseEntity<List<ReportRecordDto>> getAllReports(
            @RequestHeader("Authorization") String tokenHeader
    ) {
        log.info("Aggregator portal get all reports request");
        UserDto user = authClient.validateToken(tokenHeader).block();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ReportRecordDto> response = reportClient.getAllReports().block();
        return ResponseEntity.ok(response);
    }
}
