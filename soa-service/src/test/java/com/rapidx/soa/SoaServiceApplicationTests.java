package com.rapidx.soa;

import com.rapidx.soa.emulation.BpelOrchestrationEngine;
import com.rapidx.soa.emulation.OsbBusinessService;
import com.rapidx.soa.emulation.OsbMessageFlow;
import com.rapidx.soa.emulation.OsbProxyController;
import com.rapidx.soa.entity.BpelProcessInstance;
import com.rapidx.soa.repository.BpelProcessInstanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class SoaServiceApplicationTests {

    @Test
    void testBpelOrchestrationFlow() {
        BpelProcessInstanceRepository mockRepo = Mockito.mock(BpelProcessInstanceRepository.class);
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);

        BpelOrchestrationEngine engine = new BpelOrchestrationEngine(mockRepo, mockRestTemplate);

        Mockito.when(mockRepo.save(any(BpelProcessInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String input = "{\"accountId\": 101, \"accountName\": \"Test Oracle SOA Account\"}";
        String response = engine.executeAccountProcess(input);

        assertThat(response).contains("BPEL-");
        assertThat(response).contains("COMPLETED");
        assertThat(response).contains("Orchestration workflow completed successfully");

        Mockito.verify(mockRepo, Mockito.atLeastOnce()).save(any(BpelProcessInstance.class));
    }

    @Test
    void testOsbProxyPipeline() {
        BpelProcessInstanceRepository mockRepo = Mockito.mock(BpelProcessInstanceRepository.class);
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);

        BpelOrchestrationEngine engine = new BpelOrchestrationEngine(mockRepo, mockRestTemplate);
        OsbBusinessService businessService = new OsbBusinessService(engine);
        OsbMessageFlow messageFlow = new OsbMessageFlow(businessService);
        OsbProxyController controller = new OsbProxyController(messageFlow);

        Mockito.when(mockRepo.save(any(BpelProcessInstance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String rawInput = "{\"accountId\": 102}";
        ResponseEntity<String> response = controller.inboundProxyEndpoint(rawInput);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("BPEL-");
        assertThat(response.getBody()).contains("COMPLETED");
    }
}
