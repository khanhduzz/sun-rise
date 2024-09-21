package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.exceptions.FailedSendMailException;
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

    @Value("${default.timing-send-mail}")
    private Integer time;

    private final UserRepository userRepository;
    private final Encoder encoder;

    private final JavaMailSender javaMailSender;

    @Override
    public String sendEmail(VerificationByEmail verification) {
        User user = userRepository.findByEmailOrPhone(verification.getEmail());
        if (user == null) {
            return "Email không tồn tại người dùng!";
        }

        try {
            if (Objects.requireNonNull(VerificationByEmail.fromString(encoder.decode(user.getVerificationCode())))
                .getRequestTime().plusSeconds(time).isAfter(LocalDateTime.now())) {
                return "Vui lòng không spam!";
            }
        } catch (Exception e) {
            return "Lỗi hệ thống";
        }

        String code;
        try {
            code = encoder.encode(verification.toString());
        } catch (Exception e) {
            return "Lỗi email!";
        }

        user.setVerificationCode(code);
        userRepository.save(user);

        Thread thread = new Thread(() -> {
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
                throw new FailedSendMailException("Failed while sending email!");
            }
        });
        thread.start();

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

        if (!Objects.equals(user.getVerificationCode(), code)) {
            return "Lỗi trong quá trình xác thực!";
        }

        if (verification.getRequestTime().plusSeconds(time).isBefore(LocalDateTime.now())) {
            return "Vượt quá thời gian xác thực!";
        }

        return null;
    }

    @Override
    public String getEmailFromCode(String code) {
        try {
            return VerificationByEmail.fromString(encoder.decode(code)).getEmail();
        } catch (Exception e) {
            return null;
        }
    }
}
