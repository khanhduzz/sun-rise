package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;

public interface EmailService {
    String sendEmail(VerificationByEmail verification);

    String checkCode(String code);

    String getEmailFromCode(String code);
}
