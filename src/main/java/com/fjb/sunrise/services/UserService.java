package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.models.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    String checkRegister(RegisterRequest registerRequest);

    String changePassword(String email, String password);

    User createUserByAdmin(EditProfileByAdminDTO byAdminDTO);

    boolean updateUserByAdmin(EditProfileByAdminDTO byAdminDTO);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUserById(Long id);

    void deactivateUserById(Long id);

    void activateUserById(Long id);

    Page<UserResponseDTO> getUserList(DataTableInputDTO payload);

    UserResponseDTO getInfor();

    boolean editUser(UserResponseDTO userResponseDTO);

    // New method to update avatar
    boolean updateAvatar(MultipartFile avatarFile) throws IOException;
}
