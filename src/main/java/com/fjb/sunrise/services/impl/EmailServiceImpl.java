package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.exceptions.FailedSendMailException;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.EmailService;
import com.fjb.sunrise.utils.Encoder;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String emailServer;

    @Value("${email.verify-link}")
    private String emailLink;

    private static final int TIME = 300;

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
            if (user.getVerificationCode() != null) {
                VerificationByEmail verification1 = VerificationByEmail
                    .fromString(encoder.decode(user.getVerificationCode()));
                if (verification1 != null
                    && verification.getRequestTime().plusSeconds(TIME).isAfter(LocalDateTime.now())) {
                    return "Email đang được gửi, vui lòng đợi 30 giây!";
                }
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

        sendMailAsync(verification, code);

        return "Gửi mail thành công! \nVui lòng kiểm tra email của bạn!";
    }

    private void sendMailAsync(VerificationByEmail verification, String code) {
        Thread thread = new Thread(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h3>Thay đổi mật khẩu</h3>"
                + "<p>Nhấn vào nút dưới đây để thay đổi mật khẩu:</p>"
                + "<a href=\"" + emailLink + code + "\" "
                + "style=\"display:inline-block;"
                + "padding:10px 20px;"
                + "background-color:#4CAF50;"
                + "color:white;"
                + "text-decoration:none;border-radius:5px;\">"
                + "Thay đổi mật khẩu</a>";

            try {
                helper.setText(htmlMsg, true); // true để chỉ định đây là HTML
                helper.setTo(verification.getEmail());
                helper.setSubject("Thay đổi mật khẩu");
                helper.setFrom(emailServer);

                javaMailSender.send(mimeMessage);
            } catch (Exception e) {
                throw new FailedSendMailException("Failed while sending email!");
            }
        });
        thread.start();
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

        if (verification.getRequestTime().plusSeconds(TIME).isBefore(LocalDateTime.now())) {
            return "Vượt quá thời gian xác thực!";
        }

        return null;
    }

    @Override
    public String getEmailFromCode(String code) {
        try {
            return Objects.requireNonNull(VerificationByEmail.fromString(encoder.decode(code))).getEmail();
        } catch (Exception e) {
            return null;
        }
    }
}
