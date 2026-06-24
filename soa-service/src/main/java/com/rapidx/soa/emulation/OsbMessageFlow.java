package com.rapidx.soa.emulation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OsbMessageFlow {

    private final OsbBusinessService businessService;

    public OsbMessageFlow(OsbBusinessService businessService) {
        this.businessService = businessService;
    }

    public String processMessage(String rawRequest) {
        log.info("[OSB MESSAGE FLOW] Commencing message flow pipeline processing.");

        // 1. Validation Stage
        log.info("[OSB PIPELINE: Validation Stage] Validating inbound payload schema...");
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid payload: Inbound payload is empty");
        }

        // 2. Transformation Stage (Simulating XQuery / XSLT transform)
        log.info("[OSB PIPELINE: Transformation Stage] Applying XSLT / XQuery message transformations...");
        String transformed = transformPayload(rawRequest);

        // 3. Routing Stage
        log.info("[OSB PIPELINE: Routing Stage] Routing message to OSB Business Service");
        return businessService.routeToBpelProcess(transformed);
    }

    private String transformPayload(String input) {
        return String.format("{\"osbWrapper\": {\"source\": \"OSB_GATEWAY\", \"data\": %s}}", input);
    }
}
