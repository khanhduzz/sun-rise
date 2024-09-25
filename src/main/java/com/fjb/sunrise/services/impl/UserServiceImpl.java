package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.exceptions.DuplicatedException;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.mappers.UserMapper;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Value("${default.admin-create-key}")
    private String key;
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileSystemStorageService fileSystemStorageService;

    @Override
    public String checkRegister(RegisterRequest registerRequest) {
        //check already exist email or phone
        if (userRepository.existsUserByEmailOrPhone(registerRequest.getEmail(), registerRequest.getPhone())) {
            throw new DuplicatedException("Email hoặc số điện thoại đã được đăng ký!");
        }

        User user = mapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(EStatus.ACTIVE);

        // check password start with create admin key -> create with role admin
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
        user.setVerificationCode(null);
        userRepository.save(user);

        return null;
    }

    @Override
    public User createUserByAdmin(CreateAndEditUserByAdminDTO byAdminDTO) {
        User user = mapper.toEntityCreateByAdmin(byAdminDTO);
        user.setUsername(byAdminDTO.getUsername());
        user.setPassword(passwordEncoder.encode(byAdminDTO.getPassword()));
        user.setFirstname(byAdminDTO.getFirstname());
        user.setLastname(byAdminDTO.getLastname());
        user.setEmail(byAdminDTO.getEmail());
        user.setPhone(byAdminDTO.getPhone());
        user.setCreatedBy(byAdminDTO.getCreatedBy());
        user.setCreatedDate(byAdminDTO.getCreatedDate());
        user.setRole(ERole.valueOf(byAdminDTO.getRole()));
        user.setStatus(EStatus.valueOf(byAdminDTO.getStatus()));
        return userRepository.save(user);
    }

    @Override
    public boolean updateUserByAdmin(CreateAndEditUserByAdminDTO byAdminDTO) {
        User user = userRepository.findById(byAdminDTO.getId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (byAdminDTO.getImageName() != null) {
            fileSystemStorageService.store(byAdminDTO.getImageName());
            user.setImageName(byAdminDTO.getImageName().getOriginalFilename());
        }
        System.out.println("This is file name" + byAdminDTO.getImageName().getOriginalFilename());
        user.setUsername(byAdminDTO.getUsername());
        user.setFirstname(byAdminDTO.getFirstname());
        user.setLastname(byAdminDTO.getLastname());
        user.setEmail(byAdminDTO.getEmail());
        user.setPhone(byAdminDTO.getPhone());
        user.setRole(ERole.valueOf(byAdminDTO.getRole()));
        user.setStatus(EStatus.valueOf(byAdminDTO.getStatus()));

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
            throw new NotFoundException("User not found");
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
        final String keyword = payload.getSearch().getOrDefault("value", "");
        Specification<User> specs = null;

        if (StringUtils.hasText(keyword)) {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            specs = (root, query, builder) ->
                builder.or(
                    builder.like(builder.lower(root.get("username")), likePattern),
                    builder.like(builder.lower(root.get("phone")), likePattern),
                    builder.like(builder.lower(root.get("email")), likePattern)
                );
        }
        return userRepository.findAll(specs, pageable);
    }

    @Override
    public UserResponseDTO getInfor() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailOrPhone(name);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        UserResponseDTO responseDTO = userMapper.toUserResponse(user);
        responseDTO.setRole(user.getRole().toString());
        return responseDTO;
    }

    @Override
    public boolean editUser(UserResponseDTO userResponseDTO) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailOrPhone(name);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        user.setUsername(userResponseDTO.getUsername());
        user.setFirstname(userResponseDTO.getFirstname());
        user.setLastname(userResponseDTO.getLastname());
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean checkIsEmailDuplicate(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Override
    public boolean checkPhoneIsDuplicate(String phone) {
        return userRepository.existsUserByPhone(phone);
    }

    @Override
    public User getUserByEmailOrPhone(String emailOrPhone) {
        User user = userRepository.findByEmailOrPhone(emailOrPhone);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or phone: " + emailOrPhone);
        }
        return user;
    }

    @Override
    public String processPasswordChange(String oldPassword, String newPassword) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmailOrPhone(name);

        if (currentUser == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new DuplicatedException("Mật khẩu cũ không chính xác");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return null;
    }

    @Override
    public List<User> findAllNormalUser() {
        return userRepository.findAllByRole(ERole.USER);
    }

}

