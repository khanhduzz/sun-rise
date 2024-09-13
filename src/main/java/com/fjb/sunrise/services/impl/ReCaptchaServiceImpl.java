package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.services.ReCaptchaService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReCaptchaServiceImpl implements ReCaptchaService {
    @Value("${default.recaptcha-secret-key}")
    private String recaptchaSecretKey;

    private static final String URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    public boolean validateRecaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String url = URL + "?secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;
        String response = restTemplate.postForObject(url, null, String.class);
        assert response != null;
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        return jsonObject.get("success").getAsBoolean();
    }
}
