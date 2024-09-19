package com.fjb.sunrise.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserReporitoryTest {
    @Autowired
    private UserRepository userRepository;

    private static final String EMAIL_TEST = "test@test.com";
    private static final String PHONE_TEST = "0123456789";

    User user = new User();
    @BeforeEach
    void setup() {
        user.setFirstname("He");
        user.setLastname("Ha");
        user.setEmail(EMAIL_TEST);
        user.setPassword("12345");
        user.setRole(ERole.USER);
        user.setStatus(EStatus.ACTIVE);
        user.setPhone(PHONE_TEST);
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(user);
    }

    @Test
    void AlreadyExsistUser() {
        User emailUser = userRepository.findByEmailOrPhone(EMAIL_TEST);
        assertNotNull(emailUser);
        assertEquals(EMAIL_TEST, emailUser.getEmail());

        User phoneUser = userRepository.findByEmailOrPhone(PHONE_TEST);
        assertNotNull(phoneUser);
        assertEquals(PHONE_TEST, phoneUser.getPhone());
    }
}
