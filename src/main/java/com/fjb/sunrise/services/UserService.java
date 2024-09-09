package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.models.User;
import java.util.List;

public interface UserService {
    boolean checkRegister(RegisterRequest registerRequest);

    User createUserByAdmin(EditProfileByAdminDTO byAdminDTO);

    boolean updateUserByAdmin(EditProfileByAdminDTO byAdminDTO);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUserById(Long id);

    void deactivateUserById(Long id);
}
