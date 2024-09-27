package com.fjb.sunrise.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ReCaptchaServiceTest {
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private ReCaptchaService recaptchaService;

    @Value("${default.recaptcha-secret-key}")
    private String siteKey;
    @Test
    void validateRecaptcha_whenCaptchaDisabled_ThenReturnTrue() {
        ReflectionTestUtils.setField(recaptchaService, "captchaEnable", "false");

        boolean result = recaptchaService.validateRecaptcha("any-response");
        Assertions.assertTrue(result);
    }

    @Test
    void validateRecaptcha_whenCaptchaEnabledAndValidResponse_ThenReturnTrue() {
        ReflectionTestUtils.setField(recaptchaService, "captchaEnable", "true");
        ReflectionTestUtils.setField(recaptchaService, "recaptchaSecretKey", siteKey);
        ReflectionTestUtils.setField(recaptchaService, "restTemplate", restTemplate);

        String recaptchaResponse = "valid-response";
        String mockApiResponse = "{\"success\": true}";
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.isNull(), Mockito.eq(String.class)))
            .thenReturn(mockApiResponse);

        boolean result = recaptchaService.validateRecaptcha(recaptchaResponse);

        Assertions.assertTrue(result);
    }

    @Test
    void validateRecaptcha_whenCaptchaEnabledAndInvalidResponse_ThenReturnFalse() {
        ReflectionTestUtils.setField(recaptchaService, "captchaEnable", "true");

        String recaptchaResponse = "invalid-response";
        String mockApiResponse = "{\"success\": false}";

        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.isNull(), Mockito.eq(String.class)))
            .thenReturn(mockApiResponse);

        boolean result = recaptchaService.validateRecaptcha(recaptchaResponse);

        Assertions.assertFalse(result);
    }
}
