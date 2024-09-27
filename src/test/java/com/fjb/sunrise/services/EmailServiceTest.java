package com.fjb.sunrise.services;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.eq;

import com.fjb.sunrise.dtos.requests.VerificationByEmail;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailServiceTest {
    @Autowired
    EmailService emailService;

    @MockBean
    UserRepository userRepository;
    @Autowired
    Encoder encoder;

    @Test
    void sendEmail_WhenEmailNotExist_ThenReturnErrorMessage() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        String message = emailService.sendEmail(verification);

        Assertions.assertEquals("Email không tồn tại người dùng!", message);
    }

    @Test
    void sendEmail_WhenEmailExistedAndDoNotSend_ThenReturnNotifyMessage() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        User user = Instancio.of(User.class)
            .set(field(User::getEmail), email)
            .set(field(User::getVerificationCode), null)
            .create();

        VerificationByEmail verification = Instancio.of(VerificationByEmail.class)
                .set(field(VerificationByEmail::getEmail), email)
                .set(field(VerificationByEmail::getRequestTime), LocalDateTime.now())
                .create();

        Mockito.when(userRepository.findByEmailOrPhone(eq(verification.getEmail()))).thenReturn(user);

        String actualMessage = emailService.sendEmail(verification);

        String expectMessage = "Gửi mail thành công! \nVui lòng kiểm tra email của bạn!";

        Assertions.assertEquals(expectMessage, actualMessage);
    }

    @Test
    void sendEmail_WhenSendMail_ThenReturnAvoidSpam()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now().minusHours(1));
        User user = Instancio.of(User.class).create();
        user.setEmail(email);
        user.setVerificationCode(encoder.encode(verification.toString()));

        Mockito.when(userRepository.findByEmailOrPhone(email)).thenReturn(user);
        verification.setRequestTime(LocalDateTime.now());
        String message = emailService.sendEmail(verification);

        Assertions.assertEquals("Email đang được gửi, vui lòng đợi 30 giây!", message);
    }

    @Test
    void checkCode_WhenCodeIsInvalid_ThenReturnErrorMessage() {
        String invalidCode = "invalidCode";

        String actualMessage = emailService.checkCode(invalidCode);

        Assertions.assertEquals("Vui lòng thử lại!", actualMessage);
    }

    @Test
    void checkCode_WhenVerificationIsNull_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String code = encoder.encode("skdfjbn ưa,ẻm ndfnoewanrflsdnflskdfns");

        Mockito.when(VerificationByEmail.fromString(encoder.decode(code))).thenReturn(null);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertEquals("Vui lòng thử lại!", actualMessage);
    }

    @Test
    void checkCode_WhenUserNotFound_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
        String code = encoder.encode("skdfjbn ưa,ẻm ndfnoewanrflsdnflskdfns");
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());

        try (MockedStatic<VerificationByEmail> mockedVerification = Mockito.mockStatic(VerificationByEmail.class)) {
            mockedVerification.when(() -> VerificationByEmail.fromString(encoder.decode(code)))
                .thenReturn(verification);

            Mockito.when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(null);

            String actualMessage = emailService.checkCode(code);

            Assertions.assertEquals("Email chưa được đăng ký!", actualMessage);
        }
    }

    @Test
    void checkCode_WhenCodeDoesNotMatch_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
        String code = encoder.encode("skdfjbn ưa,ẻm ndfnoewanrflsdnflskdfns");
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        User user = Instancio.of(User.class)
            .set(field(User::getEmail), email)
            .set(field(User::getVerificationCode), code + "fail")
            .create();

        try (MockedStatic<VerificationByEmail> mockedVerification = Mockito.mockStatic(VerificationByEmail.class)) {
            mockedVerification.when(() -> VerificationByEmail.fromString(encoder.decode(code)))
                .thenReturn(verification);

            Mockito.when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(user);

            String actualMessage = emailService.checkCode(code);

            Assertions.assertEquals("Lỗi trong quá trình xác thực!", actualMessage);
        }
    }
    @Test
    void checkCode_WhenVerificationExpired_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
        String code = encoder.encode("skdfjbn ưa,ẻm ndfnoewanrflsdnflskdfns");
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now().minusHours(1));
        User user = Instancio.of(User.class)
            .set(field(User::getEmail), email)
            .set(field(User::getVerificationCode), code)
            .create();

        try (MockedStatic<VerificationByEmail> mockedVerification = Mockito.mockStatic(VerificationByEmail.class)) {
            mockedVerification.when(() -> VerificationByEmail.fromString(encoder.decode(code)))
                .thenReturn(verification);

            Mockito.when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(user);

            String actualMessage = emailService.checkCode(code);

            Assertions.assertEquals("Vượt quá thời gian xác thực!", actualMessage);
        }
}
        @Test
        void checkCode_WhenCodeIsValid_ThenReturnNull ()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
            String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
            String code = encoder.encode("skdfjbn ưa,ẻm ndfnoewanrflsdnflskdfns");
            VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
            User user = Instancio.of(User.class)
                .set(field(User::getEmail), email)
                .set(field(User::getVerificationCode), code)
                .create();

            try (MockedStatic<VerificationByEmail> mockedVerification = Mockito.mockStatic(VerificationByEmail.class)) {
                mockedVerification.when(() -> VerificationByEmail.fromString(encoder.decode(code)))
                    .thenReturn(verification);

                Mockito.when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(user);

                String actualMessage = emailService.checkCode(code);

                Assertions.assertNull(actualMessage);
            }
        }

    @Test
    void getEmailFromCode_WhenCodeIsValid_ThenReturnEmail()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        String code = encoder.encode(verification.toString());
        try (MockedStatic<VerificationByEmail> mockedVerification = Mockito.mockStatic(VerificationByEmail.class)) {
            mockedVerification.when(() -> VerificationByEmail.fromString(encoder.decode(code)))
                .thenReturn(verification);

            String actualEmail = emailService.getEmailFromCode(code);

            Assertions.assertEquals(email, actualEmail);
        }
    }

    @Test
    void getEmailFromCode_WhenCodeIsInvalid_ThenReturnNull() {
        String invalidCode = "invalidCode";

        String actualEmail = emailService.getEmailFromCode(invalidCode);

        Assertions.assertNull(actualEmail);
    }

}
