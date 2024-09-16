package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.responses.UserFullPageResponse;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    // Display user information for editing
    @GetMapping("/edit-infor")
    public ModelAndView getUserInfo() {
        ModelAndView modelAndView = new ModelAndView();
        UserResponseDTO userResponseDTO = userService.getInfor();
        modelAndView.addObject("userInfor", new UserResponseDTO());
        modelAndView.addObject("userInfor", userResponseDTO);
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);
        return modelAndView;
    }

    // Handle editing user information
    @PostMapping("/edit-infor")
    public ModelAndView editUserInfo(@ModelAttribute("userInfor") UserResponseDTO userResponseDTO) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);
        boolean editInfor = userService.editUser(userResponseDTO);
        if (editInfor) {
            modelAndView.setViewName(Constants.ApiConstant.TRANSACTION_INDEX);
        } else {
            modelAndView.addObject("error", "Failed to update user");
        }
        return modelAndView;
    }

    // Handle avatar upload and update
    @PostMapping("/edit-avatar")
    public ModelAndView changeAvatar(@RequestParam("avatar") MultipartFile avatarFile, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);

        try {
            // Call the service to update the avatar
            boolean isUpdated = userService.updateAvatar(avatarFile);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute("message", "Avatar updated successfully");
                modelAndView.setViewName("redirect:/user/edit-infor");
            } else {
                modelAndView.addObject("error", "Failed to update avatar");
            }

        } catch (IOException e) {
            modelAndView.addObject("error", "An error occurred while processing the file: " + e.getMessage());
        }

        return modelAndView;
    }

    // Additional admin features

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    public ModelAndView adminDashboard() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_VIEW);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-user-by-admin")
    public ModelAndView addUserByAdmin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("newUser", new EditProfileByAdminDTO());
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_ADD_NEW_USER);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-user-by-admin")
    public ModelAndView doAddUserByAdmin(@ModelAttribute("newUser") EditProfileByAdminDTO newUser) {
        ModelAndView modelAndView = new ModelAndView();
        userService.createUserByAdmin(newUser);
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_ADD_NEW_USER);
        return modelAndView;
    }

    @PostMapping("/page")
    @ResponseBody
    public UserFullPageResponse getPage(@RequestBody DataTableInputDTO payload) {
        Page<UserResponseDTO> transactionPage = userService.getUserList(payload);
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
    public ModelAndView doEditUserByAdmin(@PathVariable("id") Long id,
                                          @ModelAttribute("userDetail") EditProfileByAdminDTO userDTO,
                                          BindingResult bindingResult) {
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

            userDTO.setId(id);
            boolean updated = userService.updateUserByAdmin(userDTO);

            if (updated) {
                modelAndView.setViewName(Constants.ApiConstant.ADMIN_REDIRECT);
            } else {
                modelAndView.addObject(Constants.ErrorCode.ERROR, "Failed to update user");
            }
        } catch (Exception e) {
            modelAndView.addObject(Constants.ErrorCode.ERROR, "An error occurred: " + e.getMessage());
        }

        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUserByAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(Constants.ErrorCode.ERROR, e.getMessage());
        }
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deactivate/{id}")
    public String deactivateUserByAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUserById(id);
            redirectAttributes.addFlashAttribute("message", "User deactivated successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(Constants.ErrorCode.ERROR, e.getMessage());
        }
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/activate/{id}")
    public String activateUserByAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUserById(id);
            redirectAttributes.addFlashAttribute("message", "User activated successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }
}
