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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Directory where avatar images are stored
    @Value("${user.avatar.directory}")
    private String avatarDirectory;

    @Override
    public String checkRegister(RegisterRequest registerRequest) {
        // Check if email or phone already exists
        if (userRepository.existsUserByEmailOrPhone(registerRequest.getEmail(), registerRequest.getPhone())) {
            return "Email hoặc số điện thoại đã được đăng ký!";
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(EStatus.ACTIVE);

        // Check if password starts with create admin key -> create with role admin
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
    public Page<User> getUserList(DataTableInputDTO payload) {
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

        return userRepository.findAll(pageable);
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

        // Save the avatar file to the specified directory
        Path avatarPath = Paths.get(avatarDirectory, user.getId() + "_avatar.png");
        Files.write(avatarPath, avatarFile.getBytes());

        // Update the user's avatar field in the database
        user.setAvatarPath(avatarPath.toString()); // Save the path to the database
        userRepository.save(user);

        return true;
    }
}
