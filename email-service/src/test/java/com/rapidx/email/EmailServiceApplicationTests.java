package com.rapidx.email;

import com.rapidx.email.controller.EmailNotificationController;
import com.rapidx.email.dto.EmailRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class EmailServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testEmailNotificationControllerCallsExternalService() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(mockRestTemplate.postForEntity(eq("https://httpbin.org/post"), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{ \"success\": true }"));

        EmailNotificationController controller = new EmailNotificationController(mockRestTemplate);

        EmailRequest request = new EmailRequest();
        request.setRecipient("test@rapidx.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");

        ResponseEntity<String> response = controller.sendEmail(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("Email successfully sent.");

        Mockito.verify(mockRestTemplate).postForEntity(eq("https://httpbin.org/post"), any(EmailRequest.class), eq(String.class));
    }
}
