package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.utils.Encoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailServiceTest {
    @Autowired
    EmailService emailService;

    @Mock
    UserRepository userRepository;
    @Mock
    Encoder encoder;
    @Mock
    JavaMailSender javaMailSender;

    @Test
    void sendEmail_WhenEmailNotExist_ThenReturnErrorMessage() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        String message = emailService.sendEmail(verification);

        Assertions.assertEquals("Email không tồn tại người dùng!", message);
    }
}
