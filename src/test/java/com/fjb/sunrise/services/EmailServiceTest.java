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
    void sendEmail_WhenSended_ThenReturnAvoidSpam()
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
    void checkCode_WhenCodeIsInvalid_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String invalidCode = "invalidCode";

        Mockito.when(encoder.decode(invalidCode)).thenThrow(new IllegalArgumentException());

        String actualMessage = emailService.checkCode(invalidCode);

        Assertions.assertEquals("Vui lòng thử lại!", actualMessage);
    }

    @Test
    void checkCode_WhenVerificationIsNull_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String code = "validCode";

        // Giả lập hành vi của encoder trả về giá trị null
        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(null);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertEquals("Vui lòng thử lại!", actualMessage);
    }

    @Test
    void checkCode_WhenUserNotFound_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = "test@example.com";
        String code = "validCode";

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());

        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(verification);
        Mockito.when(userRepository.findByEmailOrPhone(email)).thenReturn(null);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertEquals("Email chưa được đăng ký!", actualMessage);
    }

    @Test
    void checkCode_WhenCodeDoesNotMatch_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = "test@example.com";
        String code = "validCode";
        String incorrectVerificationCode = "incorrectCode";

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        User user = new User();
        user.setEmail(email);
        user.setVerificationCode(incorrectVerificationCode);

        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(verification);
        Mockito.when(userRepository.findByEmailOrPhone(email)).thenReturn(user);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertEquals("Lỗi trong quá trình xác thực!", actualMessage);
    }

    @Test
    void checkCode_WhenVerificationExpired_ThenReturnErrorMessage()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = "test@example.com";
        String code = "validCode";

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now().minusSeconds(5000 + 1));
        User user = new User();
        user.setEmail(email);
        user.setVerificationCode(code);

        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(verification);
        Mockito.when(userRepository.findByEmailOrPhone(email)).thenReturn(user);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertEquals("Vượt quá thời gian xác thực!", actualMessage);
    }

    @Test
    void checkCode_WhenCodeIsValid_ThenReturnNull()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = "test@example.com";
        String code = "validCode";

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());
        User user = new User();
        user.setEmail(email);
        user.setVerificationCode(code);

        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(verification);
        Mockito.when(userRepository.findByEmailOrPhone(email)).thenReturn(user);

        String actualMessage = emailService.checkCode(code);

        Assertions.assertNull(actualMessage);
    }

    @Test
    void getEmailFromCode_WhenCodeIsValid_ThenReturnEmail()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String email = "test@example.com";
        String code = "validCode";

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());

        Mockito.when(encoder.decode(code)).thenReturn("encodedVerification");
        Mockito.when(VerificationByEmail.fromString("encodedVerification")).thenReturn(verification);

        String actualEmail = emailService.getEmailFromCode(code);

        Assertions.assertEquals(email, actualEmail);
    }

    @Test
    void getEmailFromCode_WhenCodeIsInvalid_ThenReturnNull()
        throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
        InvalidKeyException {
        String invalidCode = "invalidCode";

        Mockito.when(encoder.decode(invalidCode)).thenThrow(new IllegalArgumentException());

        String actualEmail = emailService.getEmailFromCode(invalidCode);

        Assertions.assertNull(actualEmail);
    }

}
