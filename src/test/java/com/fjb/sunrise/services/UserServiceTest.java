package com.fjb.sunrise.services;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(false);

        final String actualResult = userService.checkRegister(request);

        assertNull(actualResult);
    }

    @Test
    void checkRegister_WhenEmailFieldIsExisted_ThenResultWillReturnException() {
        User user = new User();
        user.setLastname(request.getLastname());
        user.setFirstname(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(Instancio.of(String.class).supply(Select.allStrings(), x-> "0" + x.digits(9)).create());

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
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

        when(userRepository.save(user)).thenReturn(user);

        when(userRepository.existsUserByEmailOrPhone(eq(request.getEmail()), eq(request.getPhone())))
            .thenReturn(true);

        Exception duplicatedException = assertThrows(DuplicatedException.class, () -> {
            userService.checkRegister(request);
        });
        assertEquals("Email hoặc số điện thoại đã được đăng ký!", duplicatedException.getMessage());
    }

    @Test
    void checkRegister_WhenRegisterWithAdminKey_ThenResultWillReturnUserHasRoleAdmin() {
        request.setPassword(Instancio.of(String.class).supply(Select.allStrings(), x-> adminCreateKey + anyString()).create());
        when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(false);

        userService.checkRegister(request);
        verify(userRepository).save(Mockito.argThat(user ->
            ERole.ADMIN.equals(user.getRole())
        ));
    }

    @Test
    void checkRegister_WhenRegisterWithoutAdminKey_ThenResultWillReturnUserHasRoleUser() {
        when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(false);

        userService.checkRegister(request);
        verify(userRepository).save(Mockito.argThat(user ->
            ERole.USER.equals(user.getRole())
        ));
    }

    @Test
    void changePassword_WhenEmailNotExist_ThenResultNotFoundException() {
        String email = Instancio.of(String.class).generate(Select.allStrings(), x-> x.net().email()).create();
        when(userRepository.existsUserByEmail(email)).thenReturn(false);

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

        when(userRepository.findByEmailOrPhone(eq(email))).thenReturn(user);

        userService.changePassword(email, newPassword);

        verify(userRepository).save(Mockito.argThat(savedUser ->
            passwordEncoder.matches(newPassword, savedUser.getPassword())
        ));

        verify(userRepository).save(Mockito.argThat(savedUser ->
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
        when(userRepository.save(any())).thenReturn(existingUser);

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
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.updateUserByAdmin(dto);

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserByAdmin_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        CreateAndEditUserByAdminDTO dto = Instancio.of(CreateAndEditUserByAdminDTO.class)
            .set(field(CreateAndEditUserByAdminDTO::getId), 99L)
            .create();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserByAdmin(dto));
    }

    @Test
    void deactivateUserById_WhenUserExists_ShouldSetStatusToNotActive() {
        Long userId = 1L;
        User user = Instancio.of(User.class)
            .set(field(User::getStatus), EStatus.ACTIVE)
            .create();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUserById(userId);

        assertEquals(EStatus.NOT_ACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deactivateUserById(userId));
    }

    @Test
    void getUserByEmailOrPhone_WhenUserExists_ShouldReturnUser() {
        String emailOrPhone = "test@example.com";
        User user = Instancio.of(User.class).set(field(User::getEmail), emailOrPhone).create();
        when(userRepository.findByEmailOrPhone(emailOrPhone)).thenReturn(user);

        User result = userService.getUserByEmailOrPhone(emailOrPhone);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmailOrPhone_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        String emailOrPhone = "nonexistent@example.com";

        when(userRepository.findByEmailOrPhone(emailOrPhone)).thenReturn(null);

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

        when(userRepository.findByEmailOrPhone(username)).thenReturn(user);
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

        when(userRepository.findByEmailOrPhone(username)).thenReturn(null);

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

        when(userRepository.findByEmailOrPhone(username)).thenReturn(user);

        boolean result = userService.editUser(userResponseDTO);

        assertTrue(result);
        assertEquals("newUsername", user.getUsername());
        assertEquals("NewFirstName", user.getFirstname());
        assertEquals("NewLastName", user.getLastname());
        verify(userRepository).save(user);
    }

    @Test
    void editUser_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String username = "nonexistent@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));

        when(userRepository.findByEmailOrPhone(username)).thenReturn(null);

        UserResponseDTO userResponseDTO = Instancio.of(UserResponseDTO.class).create();

        assertThrows(NotFoundException.class, () -> userService.editUser(userResponseDTO));
    }

    @Test
    void checkIsEmailDuplicate_WhenEmailExists_ShouldReturnTrue() {
        String email = "test@example.com";

        when(userRepository.existsUserByEmail(email)).thenReturn(true);

        boolean result = userService.checkIsEmailDuplicate(email);

        assertTrue(result);
    }

    @Test
    void checkIsEmailDuplicate_WhenEmailDoesNotExist_ShouldReturnFalse() {
        String email = "unique@example.com";

        when(userRepository.existsUserByEmail(email)).thenReturn(false);

        boolean result = userService.checkIsEmailDuplicate(email);

        assertFalse(result);
    }

    @Test
    void checkPhoneIsDuplicate_WhenPhoneExists_ShouldReturnTrue() {
        String phone = "0123456789";

        when(userRepository.existsUserByPhone(phone)).thenReturn(true);

        boolean result = userService.checkPhoneIsDuplicate(phone);

        assertTrue(result);
    }

    @Test
    void checkPhoneIsDuplicate_WhenPhoneDoesNotExist_ShouldReturnFalse() {
        String phone = "0987654321";

        when(userRepository.existsUserByPhone(phone)).thenReturn(false);

        boolean result = userService.checkPhoneIsDuplicate(phone);

        assertFalse(result);
    }

    @Test
    void processPasswordChange_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123!";
        String name = "nonexistent@example.com";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name, null));

        when(userRepository.findByEmailOrPhone(name)).thenReturn(null);

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

        when(userRepository.findByEmailOrPhone(name)).thenReturn(currentUser);

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

        when(userRepository.findByEmailOrPhone(name)).thenReturn(currentUser);

        userService.processPasswordChange(oldPassword, newPassword);

        verify(userRepository).save(Mockito.argThat(user ->
            passwordEncoder.matches(newPassword, user.getPassword())
        ));
    }

    @Test
    void findAllNormalUser_WhenThereAreUsers_ShouldReturnListOfUsers() {
        List<User> users = Instancio.ofList(User.class)
            .size(3)
            .set(field(User::getRole), ERole.USER)
            .create();

        when(userRepository.findAllByRole(ERole.USER)).thenReturn(users);

        List<User> result = userService.findAllNormalUser();

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(user -> user.getRole().equals(ERole.USER)));
    }

    @Test
    void findAllNormalUser_WhenNoNormalUsersExist_ShouldReturnEmptyList() {
        when(userRepository.findAllByRole(ERole.USER)).thenReturn(Collections.emptyList());

        List<User> result = userService.findAllNormalUser();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllNormalUser_WhenRepositoryThrowsException_ShouldThrowException() {
        when(userRepository.findAllByRole(ERole.USER)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.findAllNormalUser();
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void activateUserById_WhenUserExists_ShouldActivateUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setStatus(EStatus.NOT_ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUserById(userId);

        assertEquals(EStatus.ACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void activateUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userService.activateUserById(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void activateUserById_WhenUserAlreadyActive_ShouldNotChangeStatus() {
        Long userId = 3L;
        User user = new User();
        user.setId(userId);
        user.setStatus(EStatus.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUserById(userId);

        assertEquals(EStatus.ACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserById_UserExists() {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User actualUser = userService.getUserById(userId);

        assertNull(actualUser);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testDeleteUserById_Success() {
        Long userId = 1L;

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetAllUsers_NonEmptyList() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertAll("Kiểm tra danh sách người dùng",
            () -> assertNotNull(actualUsers, "Danh sách người dùng không được null"),
            () -> assertEquals(expectedUsers.size(), actualUsers.size(), "Số lượng người dùng không khớp"),
            () -> assertEquals(expectedUsers, actualUsers, "Danh sách người dùng không khớp với mong đợi")
        );

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        List<User> emptyList = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(emptyList);

        List<User> actualUsers = userService.getAllUsers();

        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserList_WhenNoSearch_ShouldReturnAllUsers() {
        List<User> users = Instancio.ofList(User.class).size(2).create();
        Page<User> userPage = new PageImpl<>(users);

        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(0);
        payload.setLength(10);
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", ""));

        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getUserList(payload);

        assertEquals(2, result.getTotalElements());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }

    @Test
    void getUserList_WhenSearchByUsername_ShouldReturnFilteredUsers() {
        User user = Instancio.of(User.class)
            .set(field(User::getUsername), "user1")
            .create();

        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users);

        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(0);
        payload.setLength(10);
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", "user1"));

        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getUserList(payload);

        assertEquals(1, result.getTotalElements());
        assertEquals("user1", result.getContent().get(0).getUsername());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }

    @Test
    void getUserList_WhenSearchByPhone_ShouldReturnFilteredUsers() {
        User user = Instancio.of(User.class)
            .set(field(User::getPhone), "0987654321")
            .create();

        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users);

        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(0);
        payload.setLength(10);
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", "0987654321"));

        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getUserList(payload);

        assertEquals(1, result.getTotalElements());
        assertEquals("0987654321", result.getContent().get(0).getPhone());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }

    @Test
    void getUserList_WhenSearchByEmail_ShouldReturnFilteredUsers() {
        User user = Instancio.of(User.class)
            .set(field(User::getEmail), "user1@example.com")
            .create();

        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users);

        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(0);
        payload.setLength(10);
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", "user1@example.com"));

        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getUserList(payload);

        assertEquals(1, result.getTotalElements());
        assertEquals("user1@example.com", result.getContent().get(0).getEmail());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }

    @Test
    void getUserList_WhenEmptyPayload_ShouldReturnEmptyList() {
        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(0);
        payload.setLength(10);
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", ""));

        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(emptyPage);

        Page<User> result = userService.getUserList(payload);

        assertEquals(0, result.getTotalElements());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }

    @Test
    void getUserList_WhenInvalidPageRequest_ShouldReturnCorrectPage() {
        // Giả sử có 3 user
        List<User> users = Instancio.ofList(User.class).size(3).create();
        Page<User> userPage = new PageImpl<>(users);

        DataTableInputDTO payload = new DataTableInputDTO();
        payload.setStart(15); // Bắt đầu từ bản ghi thứ 15
        payload.setLength(10); // Số lượng bản ghi trên mỗi trang
        payload.setOrder(Collections.emptyList());
        payload.setSearch(Collections.singletonMap("value", ""));

        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getUserList(payload);

        // Kiểm tra xem số lượng user có phải là 3 hay không
        assertEquals(3, result.getTotalElements());
        verify(userRepository).findAll((Specification<User>) any(), any(Pageable.class));
    }
}
