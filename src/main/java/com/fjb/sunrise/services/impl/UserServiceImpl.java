package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${default.admin-create-key}")
    private String key;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkRegister(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setFirstname(registerRequest.getFirstname());
        user.setPhone(registerRequest.getPhone());
        user.setLastname(registerRequest.getLastname());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if (registerRequest.getPassword().startsWith(key)) {
            user.setRole(ERole.ADMIN);
        } else {
            user.setRole(ERole.USER);
        }

        repository.save(user);

        return true;
    }
}
