package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.responses.UserFullPageResponse;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.services.impl.FirebaseStorageService;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final FirebaseStorageService firebaseStorageService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/edit-infor")
    public ModelAndView getUserInfo() {
        ModelAndView modelAndView = new ModelAndView();
        UserResponseDTO userResponseDTO = userService.getInfor();
        modelAndView.addObject("userInfor", userResponseDTO);
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);
        return modelAndView;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/edit-infor")
    public ModelAndView editUserInfo(
            @ModelAttribute("userInfor") UserResponseDTO userResponseDTO,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {

        ModelAndView modelAndView = new ModelAndView();

        try {
            // Kiểm tra và upload ảnh đại diện
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = firebaseStorageService.uploadFile(avatarFile, userResponseDTO.getId());
                userResponseDTO.setAvatarUrl(avatarUrl); // Cập nhật URL ảnh đại diện
            }

            // Cập nhật thông tin người dùng
            boolean editInfor = userService.editUser(userResponseDTO, avatarFile); // Truyền avatarFile
            if (editInfor) {
                modelAndView.setViewName(Constants.ApiConstant.TRANSACTION_INDEX);
            } else {
                modelAndView.addObject("error", "Failed to update user");
            }
        } catch (IOException e) {
            modelAndView.addObject("error", "Failed to upload avatar: " + e.getMessage());
        }

        modelAndView.setViewName(Constants.ApiConstant.USER_REDIRECT);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    public ModelAndView adminDashboard(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();
        String currentEmailOrPhone = authentication.getName();
        User currentUser = userService.getUserByEmailOrPhone(currentEmailOrPhone);
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("currentUserId", currentUser.getId());
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_VIEW);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-user-by-admin")
    public ModelAndView addUserByAdmin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("newUser", new CreateAndEditUserByAdminDTO());
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_ADD_NEW_USER);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-user-by-admin")
    public ModelAndView doAddUserByAdmin(@ModelAttribute("newUser") CreateAndEditUserByAdminDTO newUser) {
        ModelAndView modelAndView = new ModelAndView();
        boolean isDuplicateEmail = userService.checkIsEmailDuplicate(newUser.getEmail());
        if (isDuplicateEmail) {
            modelAndView.addObject("duplicateEmail", "Email này đã được đăng ký");
            modelAndView.setViewName(Constants.ApiConstant.ADMIN_ADD_NEW_USER);
            return modelAndView;
        }

        boolean isDuplicatePhone = userService.checkPhoneIsDuplicate(newUser.getPhone());
        if (isDuplicatePhone) {
            modelAndView.addObject("duplicatePhone", "Số điện thoại này đã được sử dụng");
            modelAndView.setViewName(Constants.ApiConstant.ADMIN_ADD_NEW_USER);
            return modelAndView;
        }

        userService.createUserByAdmin(newUser);
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_REDIRECT);
        return modelAndView;
    }

    @PostMapping("/page")
    @ResponseBody
    public UserFullPageResponse getPage(@RequestBody DataTableInputDTO payload) {
        Page<User> transactionPage = userService.getUserList(payload);
        UserFullPageResponse response = new UserFullPageResponse();
        response.setData(transactionPage.stream().toList());
        response.setDraw(payload.getDraw());
        response.setRecordsFiltered(transactionPage.getTotalElements());
        response.setRecordsTotal(transactionPage.getTotalElements());
        return response;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/detail-and-edit/{id}")
    public String detailAndEditByAdmin(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return Constants.ApiConstant.ADMIN_REDIRECT;
        }
        model.addAttribute("userDetail", user);
        return Constants.ApiConstant.ADMIN_DETAILS_AND_EDIT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/detail-and-edit/{id}")
    public ModelAndView doEditUserByAdmin(
            @PathVariable("id") Long id,
            @ModelAttribute("userDetail") CreateAndEditUserByAdminDTO editUserByAdminDTO,
            BindingResult bindingResult,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) { // Thêm tham số avatarFile

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_DETAILS_AND_EDIT);

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        try {
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                modelAndView.addObject("error", "User not found");
                return modelAndView;
            }

            // Cập nhật avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = firebaseStorageService.uploadFile(avatarFile, existingUser.getId());
                editUserByAdminDTO.setAvatarUrl(avatarUrl); // Cập nhật URL ảnh đại diện
            }

            editUserByAdminDTO.setId(id);
            boolean updated = userService.updateUserByAdmin(editUserByAdminDTO);

            if (updated) {
                modelAndView.setViewName(Constants.ApiConstant.ADMIN_REDIRECT);
            } else {
                modelAndView.addObject(Constants.ErrorCode.ERROR, "Failed to update user");
            }
        } catch (Exception e) {
            modelAndView.addObject(Constants.ErrorCode.ERROR, "An error occurred: " + e.getMessage());
        }
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_REDIRECT);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUserByAdmin(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deactivate/{id}")
    public String deactivateUserByAdmin(@PathVariable("id") Long id) {
        userService.deactivateUserById(id);
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/activate/{id}")
    public String activateUserByAdmin(@PathVariable("id") Long id) {
        userService.activateUserById(id);
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }
}
