package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${application.admin.default.key}")
    private String key;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public String checkRegister(RegisterRequest registerRequest) {
        if (registerRequest.getEmail().isEmpty() || registerRequest.getPhone().isEmpty() || registerRequest.getPassword().isEmpty() ||
                registerRequest.getLastname().isEmpty() || registerRequest.getFirstname().isEmpty() || registerRequest.getRePassword().isEmpty()) {
            return null;
        }
        if (!registerRequest.getPassword().equals(registerRequest.getRePassword())) {
            return null;
        }

        if (repository.findByEmailOrPhone(registerRequest.getEmail()) != null) {
            return null;
        }

        if (repository.findByEmailOrPhone(registerRequest.getPhone()) != null) {
            return null;
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setFirstname(registerRequest.getFirstname());
        user.setPhone(registerRequest.getPhone());
        user.setLastname(registerRequest.getLastname());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());

        if (registerRequest.getPassword().startsWith(key)) {
            user.setRole(ERole.ADMIN);
        } else user.setRole(ERole.USER);

        repository.save(user);
        return "true";
    }
}
