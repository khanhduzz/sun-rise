package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.mappers.UserMapper;
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
    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkRegister(RegisterRequest registerRequest) {
        //check already exist email -> unique email
        if (repository.findByEmailOrPhone(registerRequest.getEmail()) != null) {
            return false;
        }

        //check already exist phone -> unique phone
        if (repository.findByEmailOrPhone(registerRequest.getPhone()) != null) {
            return false;
        }

        User user = mapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // check password start with create admin key -> create with role admin
        if (registerRequest.getPassword().startsWith(key)) {
            user.setRole(ERole.ADMIN);
        } else {
            user.setRole(ERole.USER);
        }

        user = repository.save(user);

        return true;
    }
}
