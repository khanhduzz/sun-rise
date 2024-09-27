package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.responses.UserFullPageResponse;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

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
    public ModelAndView editUserInfo(@ModelAttribute("userInfor") UserResponseDTO userResponseDTO, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        boolean editInfor = userService.editUser(userResponseDTO);
        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/user/edit-infor");
        }
        if (editInfor) {
            modelAndView.setViewName(Constants.ApiConstant.USER_CHANGE_INFO_SUCCESS);
        } else {
            modelAndView.addObject("error", "Failed to update user");
        }
        return modelAndView;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/change-password")
    public ModelAndView changePassword() {
        ModelAndView modelAndView = new ModelAndView();
        UserResponseDTO userResponseDTO = userService.getInfor();
        modelAndView.addObject("userInfor", new UserResponseDTO());
        modelAndView.addObject("userInfor", userResponseDTO);
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);
        return modelAndView;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam("oldPassword") String oldPassword,
                                       @RequestParam("newPassword") String newPassword) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.USER_INFORMATION);
        String message = userService.processPasswordChange(oldPassword, newPassword);
        if (message != null) {
            modelAndView.setViewName(Constants.ApiConstant.USER_CHANGE_PASS_WORD_FAIL);
            modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, message);
            return modelAndView;
        } else {
            modelAndView.setViewName(Constants.ApiConstant.USER_CHANGE_PASS_WORD_SUCCESS);
        }
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
    public ModelAndView doEditUserByAdmin(@PathVariable("id") Long id,
                                          @ModelAttribute("userDetail")CreateAndEditUserByAdminDTO editUserByAdminDTO,
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
