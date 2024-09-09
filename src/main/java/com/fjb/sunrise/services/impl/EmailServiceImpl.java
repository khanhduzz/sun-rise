package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.EmailService;
import com.fjb.sunrise.utils.Encoder;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String emailServer;

    private final UserRepository userRepository;
    private final Encoder encoder;

    private final JavaMailSender javaMailSender;

    @Override
    public boolean sendEmail(VerificationByEmail verification) {
        String code;
        try {
            code = encoder.encode(verification.toString());
        } catch (Exception e) {
            return false;
        }

        User user = userRepository.findByEmailOrPhone(verification.getEmail());
        if (user == null) {
            return false;
        }
        user.setCodeVerification(code);
        userRepository.save(user);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailServer);
        simpleMailMessage.setTo(verification.getEmail());
        simpleMailMessage.setSubject("Verification Email");
        simpleMailMessage.setText("Click this link to change password: \n"
            + "http://localhost:8086/sun/auth/verify?code="
            + code);

        try {
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCode(String code) {
        VerificationByEmail verification;
        try {
            verification = VerificationByEmail.fromString(encoder.decode(code));
        } catch (Exception e) {
            return false;
        }

        if (verification == null) {
            return false;
        }

        User user = userRepository.findByEmailOrPhone(verification.getEmail());
        if (user == null) {
            return false;
        }

        if (!Objects.equals(user.getCodeVerification(), code)) {
            return false;
        }

        if (verification.getRequestTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    @Override
    public String getEmailFromCode(String code) {
        VerificationByEmail verification;
        try {
            verification = VerificationByEmail.fromString(encoder.decode(code));
        } catch (Exception e) {
            return null;
        }

        return verification.getEmail();
    }
}
