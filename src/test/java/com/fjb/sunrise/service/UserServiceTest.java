package com.fjb.sunrise.service;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.eq;

import com.fjb.sunrise.config.ApplicationConfig;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.mappers.UserMapper;
import com.fjb.sunrise.mappers.UserMapperImpl;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.services.impl.UserServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {
    @MockBean
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    //FunctionName_WhenDataHow_ThenResultHow
    @Test
    void checkRegister_WhenDataIsCorrectly_ThenResultWillReturn200WithResponse(){
        RegisterRequest request = Instancio.of(RegisterRequest.class)
            .generate(field(RegisterRequest::getEmail), x -> x.net().email())
            .supply(field(RegisterRequest::getPhone), x -> "0" + x.digits(9))
            .set(field(RegisterRequest::getPassword), "123456aA@")
            .create();

        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(false);

        final String actualResult = userService.checkRegister(request);

        Assertions.assertNull(actualResult);
    }
}
