package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;

public interface EmailService {
    boolean sendEmail(VerificationByEmail verification);

    boolean checkCode(String code);

    String getEmailFromCode(String code);
}
