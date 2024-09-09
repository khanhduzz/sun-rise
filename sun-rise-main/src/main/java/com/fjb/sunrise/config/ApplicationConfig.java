package com.fjb.sunrise.config;

import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Value("${application.admin.default.username}")
    private String adminUsername;

    @Value("${application.admin.default.password}")
    private String adminPassword;

    @Value("${application.admin.default.firstname}")
    private String adminFirstname;

    @Value("${application.admin.default.lastname}")
    private String adminLastname;

    @Value("${application.admin.default.email}")
    private String adminEmail;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "spring",
        value = "datasource.driver-class-name",
        havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner() {
        log.info("Initializing application.....");
        return args -> {
            if (!userRepository.existsByUsername(adminUsername)) {
                User user = User.builder()
                    .username(adminUsername)
                    .firstname(adminFirstname)
                    .lastname(adminLastname)
                    .email(adminEmail)
                    .password(passwordEncoder().encode(adminPassword))
                    .role(ERole.ADMIN)
                    .build();
                userRepository.save(user);
                log.warn("Create: admin user has been created: email = {}, password = {} ",
                    adminEmail, adminPassword);
            } else {
                log.warn("Admin user: email = {}, password = {} ", adminEmail, adminPassword);
            }
            log.info("Application initialization completed .....");
        };
    }
}
