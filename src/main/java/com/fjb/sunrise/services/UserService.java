package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.models.User;
import java.util.List;
import org.springframework.data.domain.Page;

public interface UserService {
    String checkRegister(RegisterRequest registerRequest);

    String changePassword(String email, String password);

    User createUserByAdmin(CreateAndEditUserByAdminDTO byAdminDTO);

    boolean updateUserByAdmin(CreateAndEditUserByAdminDTO byAdminDTO);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUserById(Long id);

    void deactivateUserById(Long id);

    void activateUserById(Long id);

    Page<User> getUserList(DataTableInputDTO payload);

    UserResponseDTO getInfor();

    boolean editUser(UserResponseDTO userResponseDTO);

    boolean checkIsEmailDuplicate(String email);

    boolean checkPhoneIsDuplicate(String phone);

    User getUserByEmailOrPhone(String emailOrPhone);

    List<User> findAllNormalUser();
}
