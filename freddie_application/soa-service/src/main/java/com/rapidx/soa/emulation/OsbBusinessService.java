package com.rapidx.soa.emulation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OsbBusinessService {

    private final BpelOrchestrationEngine bpelEngine;

    public OsbBusinessService(BpelOrchestrationEngine bpelEngine) {
        this.bpelEngine = bpelEngine;
    }

    public String routeToBpelProcess(String transformedPayload) {
        log.info("[OSB BUSINESS SERVICE] Outbound routing to BPEL Process Engine");
        return bpelEngine.executeAccountProcess(transformedPayload);
    }
}
