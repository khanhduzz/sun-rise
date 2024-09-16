package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.mappers.UserMapper;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Value("${default.admin-create-key}")
    private String key;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String checkRegister(RegisterRequest registerRequest) {
        if (userRepository.existsUserByEmailOrPhone(registerRequest.getEmail(), registerRequest.getPhone())) {
            return "Email hoặc số điện thoại đã được đăng ký!";
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(EStatus.ACTIVE);

        if (registerRequest.getPassword().startsWith(key)) {
            user.setRole(ERole.ADMIN);
        } else {
            user.setRole(ERole.USER);
        }

        userRepository.save(user);
        return null;
    }

    @Override
    public String changePassword(String email, String password) {
        User user = userRepository.findByEmailOrPhone(email);
        if (user == null) {
            return "Email chưa được đăng ký!";
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return null;
    }

    @Override
    public User createUserByAdmin(EditProfileByAdminDTO byAdminDTO) {
        User user = userMapper.toEntityByAdmin(byAdminDTO);
        user.setUsername(byAdminDTO.getUsername());
        user.setPassword(passwordEncoder.encode(byAdminDTO.getPassword()));
        user.setRole(ERole.valueOf(byAdminDTO.getRole().toUpperCase())); // Fix valueOf conversion
        user.setStatus(EStatus.valueOf(byAdminDTO.getStatus().toUpperCase())); // Fix valueOf conversion
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
        user.setRole(ERole.valueOf(byAdminDTO.getRole().toUpperCase())); // Fix valueOf conversion
        user.setStatus(EStatus.valueOf(byAdminDTO.getStatus().toUpperCase())); // Fix valueOf conversion
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
            throw new NotFoundException(Constants.ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public void activateUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(EStatus.ACTIVE);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Page<UserResponseDTO> getUserList(DataTableInputDTO payload) {
        Sort sortOpt = Sort.by(Sort.Direction.ASC, "id");
        if (!payload.getOrder().isEmpty()) {
            sortOpt = Sort.by(
                    Sort.Direction.fromString(payload.getOrder().get(0).get("dir").toUpperCase()),
                    payload.getOrder().get(0).get("colName"));
        }
        int pageNumber = payload.getStart() / 10;
        if (payload.getStart() % 10 != 0) {
            pageNumber = pageNumber - 1;
        }

        Pageable pageable = PageRequest.of(pageNumber, payload.getLength(), sortOpt);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public UserResponseDTO getInfor() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailOrPhone(name);
        return userMapper.toUserResponse(user);
    }

    @Override
    public boolean editUser(UserResponseDTO userResponseDTO) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailOrPhone(name);
        user.setUsername(userResponseDTO.getUsername());
        user.setFirstname(userResponseDTO.getFirstname());
        user.setLastname(userResponseDTO.getLastname());
        user.setPhone(userResponseDTO.getPhone());
        user.setEmail(userResponseDTO.getEmail());
        user.setAvatarImage(userResponseDTO.getAvatarImage().getBytes()); // Ensure avatar is updated
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateAvatar(MultipartFile avatarFile) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailOrPhone(username);

        if (user == null) {
            throw new NotFoundException(Constants.ErrorCode.USER_NOT_FOUND);
        }

        // Save the avatar file as a byte array
        user.setAvatarImage(avatarFile.getBytes()); // Save the byte array to the database
        userRepository.save(user);

        return true;
    }
}
