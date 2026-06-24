package com.rapidx.soa.emulation;

import com.rapidx.soa.entity.BpelProcessInstance;
import com.rapidx.soa.repository.BpelProcessInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
@Slf4j
public class BpelOrchestrationEngine {

    private final BpelProcessInstanceRepository bpelRepository;
    private final RestTemplate restTemplate;

    public BpelOrchestrationEngine(BpelProcessInstanceRepository bpelRepository, RestTemplate restTemplate) {
        this.bpelRepository = bpelRepository;
        this.restTemplate = restTemplate;
    }

    public String executeAccountProcess(String inputPayload) {
        String processId = "BPEL-" + UUID.randomUUID().toString();
        log.info("[BPEL PROCESS START] Initializing process: {} for Account Creation", processId);

        // 1. BPEL ACTIVITY: Receive
        log.info("[BPEL ACTIVITY: Receive] Inbound payload accepted.");

        // 2. BPEL ACTIVITY: Assign
        String status = "RUNNING";
        BpelProcessInstance instance = BpelProcessInstance.builder()
                .processId(processId)
                .processName("AccountProvisioningProcess")
                .status(status)
                .payload(inputPayload)
                .build();

        // 3. BPEL ACTIVITY: Invoke (Partner Link: Oracle Database for tracking)
        log.info("[BPEL ACTIVITY: Invoke] Calling PartnerLink: Oracle DB to persist process state");
        bpelRepository.save(instance);

        // 4. BPEL ACTIVITY: Invoke (Partner Link: Downstream Service simulation)
        log.info("[BPEL ACTIVITY: Invoke] Calling PartnerLink: Downstream API gateway webhook");
        boolean serviceCallSuccess = false;
        try {
            log.info("[BPEL PartnerLink Client] Dispatching request payload downstream...");
            serviceCallSuccess = true;
        } catch (Exception e) {
            log.error("[BPEL PartnerLink Error] Downstream partner link invocation failed: {}", e.getMessage());
        }

        // 5. BPEL ACTIVITY: Assign (Final Status update)
        if (serviceCallSuccess) {
            status = "COMPLETED";
        } else {
            status = "FAILED";
        }
        instance.setStatus(status);
        String finalResponse = String.format("{\"processId\": \"%s\", \"status\": \"%s\", \"message\": \"Orchestration workflow completed successfully\"}", processId, status);
        instance.setResponse(finalResponse);

        // Save updated state to database
        bpelRepository.save(instance);
        log.info("[BPEL PROCESS END] Process {} completed with status: {}", processId, status);

        return finalResponse;
    }
}
