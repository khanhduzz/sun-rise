package com.fjb.sunrise.services;

public interface ReCaptchaService {
    boolean validateRecaptcha(String recaptchaResponse);
}
