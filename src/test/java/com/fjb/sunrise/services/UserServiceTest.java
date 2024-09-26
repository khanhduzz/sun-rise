package com.fjb.sunrise.services;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.exceptions.DuplicatedException;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.impl.UserServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {
    @MockBean
    UserRepository userRepository;
    @Autowired
    UserServiceImpl userService;

    @Value("${default.admin-create-key}")
    private String adminCreateKey;

    private RegisterRequest request;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        request = Instancio.of(RegisterRequest.class)
            .generate(field(RegisterRequest::getEmail), x -> x.net().email()) // generate value base on Instancio library
            .supply(field(RegisterRequest::getPhone), x -> "0" + x.digits(9)) // generate value follow developer
//            .set(field(RegisterRequest::getPassword), "123456aA@") // set value into specific field. If Being list, setting all
            .create();
    }

    //FunctionName_WhenDataHow_ThenResultHow
    @Test
    void checkRegister_WhenDataIsNormal_ThenResultWillReturn200WithResponse(){
        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(false);

        final String actualResult = userService.checkRegister(request);

        Assertions.assertNull(actualResult);
    }

    @Test
    void checkRegister_WhenEmailFieldIsExisted_ThenResultWillReturnException() {
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(Instancio.of(String.class).supply(Select.allStrings(), x-> "0" + x.digits(9)).create());

        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(true);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
                userService.checkRegister(request);
            });
        assertEquals("Email hoặc số điện thoại đã được đăng ký!", duplicatedException.getMessage());
    }

    @Test
    void checkRegister_WhenPhoneFieldIsExisted_ThenResultWillReturnException() {
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setEmail(Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create());

        Mockito.when(userRepository.save(user)).thenReturn(user);

        Mockito
            .when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(true);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
            userService.checkRegister(request);
        });
        assertEquals("Email hoặc số điện thoại đã được đăng ký!", duplicatedException.getMessage());
    }

    @Test
    void checkRegister_WhenRegisterWithAdminKey_ThenResultWillReturnUserHasRoleAdmin() {
        request.setPassword(Instancio.of(String.class).supply(Select.allStrings(), x-> adminCreateKey + anyString()).create());
        Mockito.when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(false);

        userService.checkRegister(request);
        Mockito.verify(userRepository).save(Mockito.argThat(user ->
            ERole.ADMIN.equals(user.getRole())
        ));
    }

    @Test
    void checkRegister_WhenRegisterWithoutAdminKey_ThenResultWillReturnUserHasRoleUser() {
        Mockito.when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(false);

        userService.checkRegister(request);
        Mockito.verify(userRepository).save(Mockito.argThat(user ->
            ERole.USER.equals(user.getRole())
        ));
    }

    @Test
    void changePassword_WhenEmailNotExist_ThenResultNotFoundException() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        Mockito.when(userRepository.existsUserByEmail(email)).thenReturn(false);

        Exception notFoundEmail = assertThrows(NotFoundException.class, () -> {
            userService.changePassword(email, anyString());
        });
        assertEquals("Email chưa được đăng ký!", notFoundEmail.getMessage());
    }

    @Test
    void changePassword_WhenEmailExisted_ThenResultChangedPassword() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x -> x.net().email()).create();
        String newPassword = Instancio.of(String.class).supply(Select.allStrings(), x -> "NewPassword123!").create();

        User user = Instancio.of(User.class)
            .set(field(User::getEmail), email)
            .set(field(User::getPassword), passwordEncoder.encode("OldPassword123!"))
            .create();

        Mockito.when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(user);

        userService.changePassword(email, newPassword);

        Mockito.verify(userRepository).save(Mockito.argThat(savedUser ->
            passwordEncoder.matches(newPassword, savedUser.getPassword())
        ));

        Mockito.verify(userRepository).save(Mockito.argThat(savedUser ->
            savedUser.getVerificationCode() == null
        ));
    }

    @Test
    void createUserByAdmin_ShouldSaveUser() {
        CreateAndEditUserByAdminDTO dto = Instancio.of(CreateAndEditUserByAdminDTO.class)
            .set(field(CreateAndEditUserByAdminDTO::getEmail), "admin@example.com")
            .set(field(CreateAndEditUserByAdminDTO::getPassword), "password123")
            .set(field(CreateAndEditUserByAdminDTO::getRole), "ADMIN")
            .set(field(CreateAndEditUserByAdminDTO::getStatus), "ACTIVE")
            .create();
        User existingUser = Instancio.of(User.class).create();
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(existingUser);

        User actual = userService.createUserByAdmin(dto);

        assertEquals(actual, existingUser);
    }

    @Test
    void updateUserByAdmin_WhenUserExists_ShouldUpdateUser() {
        CreateAndEditUserByAdminDTO dto = Instancio.of(CreateAndEditUserByAdminDTO.class)
            .set(field(CreateAndEditUserByAdminDTO::getId), 1L)
            .set(field(CreateAndEditUserByAdminDTO::getRole), "ADMIN")
            .set(field(CreateAndEditUserByAdminDTO::getStatus), "ACTIVE")
            .create();

        User existingUser = Instancio.of(User.class).create();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.updateUserByAdmin(dto);

        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserByAdmin_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        CreateAndEditUserByAdminDTO dto = Instancio.of(CreateAndEditUserByAdminDTO.class)
            .set(field(CreateAndEditUserByAdminDTO::getId), 99L)
            .create();

        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserByAdmin(dto));
    }

    @Test
    void deactivateUserById_WhenUserExists_ShouldSetStatusToNotActive() {
        Long userId = 1L;
        User user = Instancio.of(User.class)
            .set(field(User::getStatus), EStatus.ACTIVE)
            .create();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUserById(userId);

        assertEquals(EStatus.NOT_ACTIVE, user.getStatus());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void deactivateUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        Long userId = 99L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deactivateUserById(userId));
    }

    @Test
    void getUserByEmailOrPhone_WhenUserExists_ShouldReturnUser() {
        String emailOrPhone = "test@example.com";
        User user = Instancio.of(User.class).set(field(User::getEmail), emailOrPhone).create();
        Mockito.when(userRepository.findByEmailOrPhone(emailOrPhone)).thenReturn(user);

        User result = userService.getUserByEmailOrPhone(emailOrPhone);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmailOrPhone_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        String emailOrPhone = "nonexistent@example.com";

        Mockito.when(userRepository.findByEmailOrPhone(emailOrPhone)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserByEmailOrPhone(emailOrPhone));
    }

    @Test
    void getInfor_WhenUserExists_ShouldReturnUserResponseDTO() {
        String username = "test@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        User user = Instancio.of(User.class)
            .set(field(User::getEmail), username)
            .set(field(User::getRole), ERole.USER)
            .create();

        Mockito.when(userRepository.findByEmailOrPhone(username)).thenReturn(user);
        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setEmail(user.getEmail());
        expectedResponse.setRole(user.getRole().name());

        UserResponseDTO result = userService.getInfor();

        assertEquals(user.getRole().toString(), result.getRole());
    }

    @Test
    void getInfor_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String username = "nonexistent@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        Mockito.when(userRepository.findByEmailOrPhone(username)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getInfor());
    }

    @Test
    void editUser_WhenUserExists_ShouldUpdateUserDetails() {
        String username = "test@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        User user = Instancio.of(User.class).create();
        UserResponseDTO userResponseDTO = Instancio.of(UserResponseDTO.class)
            .set(field(UserResponseDTO::getUsername), "newUsername")
            .set(field(UserResponseDTO::getFirstname), "NewFirstName")
            .set(field(UserResponseDTO::getLastname), "NewLastName")
            .create();

        Mockito.when(userRepository.findByEmailOrPhone(username)).thenReturn(user);

        boolean result = userService.editUser(userResponseDTO);

        assertTrue(result);
        assertEquals("newUsername", user.getUsername());
        assertEquals("NewFirstName", user.getFirstname());
        assertEquals("NewLastName", user.getLastname());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void editUser_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String username = "nonexistent@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        Mockito.when(userRepository.findByEmailOrPhone(username)).thenReturn(null);

        UserResponseDTO userResponseDTO = Instancio.of(UserResponseDTO.class).create();

        assertThrows(NotFoundException.class, () -> userService.editUser(userResponseDTO));
    }

    @Test
    void checkIsEmailDuplicate_WhenEmailExists_ShouldReturnTrue() {
        String email = "test@example.com";

        Mockito.when(userRepository.existsUserByEmail(email)).thenReturn(true);

        boolean result = userService.checkIsEmailDuplicate(email);

        assertTrue(result);
    }

    @Test
    void checkIsEmailDuplicate_WhenEmailDoesNotExist_ShouldReturnFalse() {
        String email = "unique@example.com";

        Mockito.when(userRepository.existsUserByEmail(email)).thenReturn(false);

        boolean result = userService.checkIsEmailDuplicate(email);

        assertFalse(result);
    }

    @Test
    void checkPhoneIsDuplicate_WhenPhoneExists_ShouldReturnTrue() {
        String phone = "0123456789";

        Mockito.when(userRepository.existsUserByPhone(phone)).thenReturn(true);

        boolean result = userService.checkPhoneIsDuplicate(phone);

        assertTrue(result);
    }

    @Test
    void checkPhoneIsDuplicate_WhenPhoneDoesNotExist_ShouldReturnFalse() {
        String phone = "0987654321";

        Mockito.when(userRepository.existsUserByPhone(phone)).thenReturn(false);

        boolean result = userService.checkPhoneIsDuplicate(phone);

        assertFalse(result);
    }

    @Test
    void processPasswordChange_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123!";
        String name = "nonexistent@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, null));

        Mockito.when(userRepository.findByEmailOrPhone(name)).thenReturn(null);

        Exception notFoundException = assertThrows(NotFoundException.class, () -> {
            userService.processPasswordChange(oldPassword, newPassword);
        });

        assertEquals("Người dùng không tồn tại", notFoundException.getMessage());
    }

    @Test
    void processPasswordChange_WhenOldPasswordIsIncorrect_ShouldThrowDuplicatedException() {
        String oldPassword = "incorrectOldPassword";
        String newPassword = "newPassword123!";
        String name = "test@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, null));

        User currentUser = Instancio.of(User.class)
            .set(field(User::getEmail), name)
            .set(field(User::getPassword), passwordEncoder.encode("correctOldPassword"))
            .create();

        Mockito.when(userRepository.findByEmailOrPhone(name)).thenReturn(currentUser);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
            userService.processPasswordChange(oldPassword, newPassword);
        });

        assertEquals("Mật khẩu cũ không chính xác", duplicatedException.getMessage());
    }

    @Test
    void processPasswordChange_WhenOldPasswordIsCorrect_ShouldChangePassword() {
        String oldPassword = "correctOldPassword";
        String newPassword = "newPassword123!";
        String name = "test@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, null));

        User currentUser = Instancio.of(User.class)
            .set(field(User::getEmail), name)
            .set(field(User::getPassword), passwordEncoder.encode(oldPassword))
            .create();

        Mockito.when(userRepository.findByEmailOrPhone(name)).thenReturn(currentUser);

        userService.processPasswordChange(oldPassword, newPassword);

        Mockito.verify(userRepository).save(Mockito.argThat(user ->
            passwordEncoder.matches(newPassword, user.getPassword())
        ));
    }

    @Test
    void findAllNormalUser_WhenThereAreUsers_ShouldReturnListOfUsers() {
        List<User> users = Instancio.ofList(User.class)
            .size(3)
            .set(field(User::getRole), ERole.USER)
            .create();

        Mockito.when(userRepository.findAllByRole(ERole.USER)).thenReturn(users);

        List<User> result = userService.findAllNormalUser();

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(user -> user.getRole().equals(ERole.USER)));
    }

    @Test
    void findAllNormalUser_WhenNoNormalUsersExist_ShouldReturnEmptyList() {
        Mockito.when(userRepository.findAllByRole(ERole.USER)).thenReturn(Collections.emptyList());

        List<User> result = userService.findAllNormalUser();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllNormalUser_WhenRepositoryThrowsException_ShouldThrowException() {
        Mockito.when(userRepository.findAllByRole(ERole.USER)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.findAllNormalUser();
        });

        assertEquals("Database error", exception.getMessage());
    }

}
