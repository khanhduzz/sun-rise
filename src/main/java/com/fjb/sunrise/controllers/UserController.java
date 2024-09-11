package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.user.EditProfileByAdminDTO;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    public ModelAndView adminDashboard() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.setViewName(Constants.ApiConstant.ADMIN_VIEW);
        return modelAndView;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/detail-and-edit-by-admin")
    public String detailAndEdit(@RequestParam Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("userDetail", user);
        return Constants.ApiConstant.ADMIN_DETAILS_AND_EDIT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/detail-and-edit-by-admin")
    public ModelAndView doEditUser(@RequestParam Long id,
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
                return new ModelAndView();
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
    @GetMapping("/delete-user-by-admin")
    public String deleteUserByAdmin(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(Constants.ErrorCode.ERROR, e.getMessage());
        }
        return Constants.ApiConstant.ADMIN_REDIRECT;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deactivate-user-by-admin")
    public String deactivateUserByAdmin(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUserById(id);
            redirectAttributes.addFlashAttribute("message", "User deactivated successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(Constants.ErrorCode.ERROR, e.getMessage());
        }
        return Constants.ApiConstant.ADMIN_REDIRECT;
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
}
