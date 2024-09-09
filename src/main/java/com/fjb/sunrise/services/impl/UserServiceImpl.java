package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.mappers.UserMapper;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import java.util.List;
import java.util.Optional;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkRegister(RegisterRequest registerRequest) {
        //check already exist email or phone
        if (userRepository.existsUserByEmailOrPhone(registerRequest.getEmail(), registerRequest.getPhone())) {
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

        user = userRepository.save(user);

        return true;
    }

    @Override
    public User createUserByAdmin(EditProfileByAdminDTO byAdminDTO) {
        User user = mapper.toEntityByAdmin(byAdminDTO);
        user.setUsername(byAdminDTO.getUsername());
        user.setPassword(passwordEncoder.encode(byAdminDTO.getPassword()));
        user.setRole(ERole.valueOf(byAdminDTO.getRole()));
        user.setStatus(EStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Override
    public boolean updateUserByAdmin(EditProfileByAdminDTO byAdminDTO) {
        User user = userRepository.findById(byAdminDTO.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(byAdminDTO.getUsername());
        if (!passwordEncoder.matches(byAdminDTO.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(byAdminDTO.getPassword()));
        }

        user.setFirstname(byAdminDTO.getFirstname());
        user.setLastname(byAdminDTO.getLastname());
        user.setEmail(byAdminDTO.getEmail());
        user.setPhone(byAdminDTO.getPhone());
        user.setRole(ERole.valueOf(byAdminDTO.getRole()));
        user.setStatus(EStatus.valueOf(byAdminDTO.getStatus()));
        user.setCreatedDate(byAdminDTO.getCreatedDate());
        user.setCreatedBy(byAdminDTO.getCreatedBy());
        user.setLastModifiedDate(byAdminDTO.getLastModifiedDate());
        user.setLastModifiedBy(byAdminDTO.getLastModifiedBy());

        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deactivateUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(EStatus.NOT_ACTIVE);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
