package com.rapidx.aggregator;

import com.rapidx.aggregator.dto.ActionAccountDto;
import com.rapidx.aggregator.entity.UCSliofBusCd;
import com.rapidx.aggregator.entity.UcsCntPrtyAcctRoleAssn;
import com.rapidx.aggregator.entity.UserCntrPrtyAcct;
import com.rapidx.aggregator.processor.UserCntrPrtyAcctProcessor;
import com.rapidx.aggregator.repository.UserCntrPrtyAcctRepository;
import com.rapidx.aggregator.specification.UserCntrPrtyAcctSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class AggregatorApplicationTests {

    @Autowired
    private UserCntrPrtyAcctRepository repository;

    @Autowired
    private UserCntrPrtyAcctProcessor processor;

    @Test
    void contextLoads() {
        // Basic context load validation
    }

    @Test
    @Transactional
    void testLocalRepositoryAndSpecificationAndProcessor() {
        // 1. Create Line of Business code
        UCSliofBusCd lineOfBus = UCSliofBusCd.builder()
                .liOfBus("Mortgage")
                .build();

        // 2. Create User Counterparty Account
        UserCntrPrtyAcct account = UserCntrPrtyAcct.builder()
                .nameCntprtyAcct("Test Account Inc.")
                .idOrgtn(12345)
                .ucsCntPrtyAcctRoleSts("Active")
                .build();

        // 3. Create role association
        UcsCntPrtyAcctRoleAssn roleAssn = UcsCntPrtyAcctRoleAssn.builder()
                .userCntrPrtyAcct(account)
                .uCSliofBusCd(lineOfBus)
                .build();

        account.setUcsCntPrtyAcctRoleAssn(Collections.singleton(roleAssn));

        // 4. Save entity
        UserCntrPrtyAcct saved = repository.save(account);
        assertThat(saved.getId()).isNotNull();

        // 5. Query using Specification
        Specification<UserCntrPrtyAcct> spec = UserCntrPrtyAcctSpecification.filterAccounts(12345, "Active", "Mortgage");
        Page<UserCntrPrtyAcct> filteredPage = repository.findAll(spec, PageRequest.of(0, 10));
        assertThat(filteredPage.getContent()).isNotEmpty();

        // 6. Map to DTO via Processor
        UserCntrPrtyAcct foundEntity = filteredPage.getContent().get(0);
        ActionAccountDto dto = processor.transformToDto(foundEntity);
        assertThat(dto).isNotNull();
        assertThat(dto.getOrgId()).isEqualTo(12345);
        assertThat(dto.getName()).isEqualTo("Test Account Inc.");
        assertThat(dto.getSearchKey()).contains("Test Account Inc. | Org:12345");
    }

    @Test
    void testApiServiceImplWithWebClient() {
        org.springframework.web.reactive.function.client.WebClient.Builder builder = org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.Builder.class);
        org.springframework.web.reactive.function.client.WebClient webClient = org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.class);

        org.mockito.Mockito.when(builder.build()).thenReturn(webClient);

        com.rapidx.aggregator.client.OAuthTokenCache tokenCache = org.mockito.Mockito.mock(com.rapidx.aggregator.client.OAuthTokenCache.class);
        org.mockito.Mockito.when(tokenCache.getOAuthAccessToken()).thenReturn("Bearer token");

        com.rapidx.aggregator.repository.OimSyncErrorRepository errorRepo = org.mockito.Mockito.mock(com.rapidx.aggregator.repository.OimSyncErrorRepository.class);

        com.rapidx.aggregator.service.ApiServiceImpl serviceUnderTest = new com.rapidx.aggregator.service.ApiServiceImpl(builder, tokenCache, errorRepo);

        // Mock the webClient.get() chain
        @SuppressWarnings("rawtypes")
        org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec getSpec =
                org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec.class);
        org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec headersSpec =
                org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec.class);

        org.mockito.Mockito.when(webClient.get()).thenReturn(getSpec);
        org.mockito.Mockito.when(getSpec.uri(org.mockito.Mockito.anyString())).thenReturn(headersSpec);
        org.mockito.Mockito.when(headersSpec.headers(org.mockito.Mockito.any())).thenReturn(headersSpec);

        org.mockito.Mockito.when(headersSpec.exchangeToMono(org.mockito.Mockito.any())).thenReturn(reactor.core.publisher.Mono.empty());

        serviceUnderTest.get("http://localhost:8080/accounts/update-family/1", 1L, "FAMILY_NAME_UPDATE", String.class);

        // Verify webClient.get() was called
        org.mockito.Mockito.verify(webClient).get();
        org.mockito.Mockito.verify(getSpec).uri("http://localhost:8080/accounts/update-family/1");
    }

    @Test
    void testReportClientWithWebClient() {
        org.springframework.web.reactive.function.client.WebClient.Builder builder = org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.Builder.class);
        org.springframework.web.reactive.function.client.WebClient webClient = org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.class);

        org.mockito.Mockito.when(builder.baseUrl(org.mockito.Mockito.anyString())).thenReturn(builder);
        org.mockito.Mockito.when(builder.build()).thenReturn(webClient);

        com.rapidx.aggregator.client.ReportClient clientUnderTest = new com.rapidx.aggregator.client.ReportClient(builder, "http://localhost:8086");

        // Mock post() chain
        org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec postSpec =
                org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec.class);
        org.springframework.web.reactive.function.client.WebClient.RequestBodySpec bodySpec =
                org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.RequestBodySpec.class);
        org.springframework.web.reactive.function.client.WebClient.ResponseSpec responseSpec =
                org.mockito.Mockito.mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        org.mockito.Mockito.doReturn(postSpec).when(webClient).post();
        org.mockito.Mockito.doReturn(bodySpec).when(postSpec).uri(org.mockito.Mockito.anyString());
        org.mockito.Mockito.doReturn(bodySpec).when(bodySpec).bodyValue(org.mockito.Mockito.any());
        org.mockito.Mockito.doReturn(responseSpec).when(bodySpec).retrieve();

        com.rapidx.aggregator.dto.ReportRecordDto reportDto = new com.rapidx.aggregator.dto.ReportRecordDto();
        reportDto.setTitle("Title");
        reportDto.setContent("Content");

        org.mockito.Mockito.doReturn(reactor.core.publisher.Mono.just(reportDto))
                .when(responseSpec).bodyToMono(com.rapidx.aggregator.dto.ReportRecordDto.class);

        com.rapidx.aggregator.dto.ReportRecordDto result = clientUnderTest.createReport(reportDto).block();
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getContent()).isEqualTo("Content");
    }
}
