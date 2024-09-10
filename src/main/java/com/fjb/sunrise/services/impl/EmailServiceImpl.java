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
    public String sendEmail(VerificationByEmail verification) {
        String code;
        try {
            code = encoder.encode(verification.toString());
        } catch (Exception e) {
            return "Lỗi email!";
        }

        User user = userRepository.findByEmailOrPhone(verification.getEmail());
        if (user == null) {
            return "Email không tồn tại người dùng!";
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
            return "Gửi mail không thành công!";
        }
        return "Gửi mail thành công! \nVui lòng kiểm tra email của bạn!";
    }

    @Override
    public String checkCode(String code) {
        VerificationByEmail verification;
        try {
            verification = VerificationByEmail.fromString(encoder.decode(code));
        } catch (Exception e) {
            return "Vui lòng thử lại!";
        }

        if (verification == null) {
            return "Vui lòng thử lại!";
        }

        User user = userRepository.findByEmailOrPhone(verification.getEmail());
        if (user == null) {
            return "Email chưa được đăng ký!";
        }

        if (!Objects.equals(user.getCodeVerification(), code)) {
            return "Vui lòng thử lại!";
        }

        if (verification.getRequestTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return "Vượt quá thời gian xác thực!";
        }

        return null;
    }

    @Override
    public String getEmailFromCode(String code) {
        VerificationByEmail verification;
        try {
            verification = VerificationByEmail.fromString(encoder.decode(code));
        } catch (Exception e) {
            return null;
        }

        assert verification != null;
        return verification.getEmail();
    }
}
