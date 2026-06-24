package com.rapidx.soa.emulation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/osb/proxy")
@Slf4j
public class OsbProxyController {

    private final OsbMessageFlow messageFlow;

    public OsbProxyController(OsbMessageFlow messageFlow) {
        this.messageFlow = messageFlow;
    }

    @PostMapping("/process-account")
    public ResponseEntity<String> inboundProxyEndpoint(@RequestBody String rawPayload) {
        log.info("[OSB PROXY SERVICE] Inbound HTTP POST request received at Proxy Endpoint");
        try {
            String bpelResult = messageFlow.processMessage(rawPayload);
            return ResponseEntity.ok(bpelResult);
        } catch (IllegalArgumentException e) {
            log.error("[OSB PROXY SERVICE ERROR] Validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(String.format("{\"error\": \"%s\"}", e.getMessage()));
        } catch (Exception e) {
            log.error("[OSB PROXY SERVICE ERROR] Unexpected failure in pipeline execution: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("{\"error\": \"Pipeline execution failure: %s\"}", e.getMessage()));
        }
    }
}
