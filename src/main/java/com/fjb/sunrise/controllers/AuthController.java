package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_REDIRECT_LOGIN;
import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VIEW;
import static com.fjb.sunrise.utils.Constants.ApiConstant.ERROR_MESSAGE_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.LOGIN_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.REGISTER_OBJECT;

import com.fjb.sunrise.dtos.requests.LoginRequest;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.services.EmailService;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Encoder;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping("/login")
    public ModelAndView indexLogin(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(REGISTER_OBJECT, new RegisterRequest());
        if (error != null) {
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Đăng nhập không thành công!");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView indexRegister() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(REGISTER_OBJECT, new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@ModelAttribute(REGISTER_OBJECT) RegisterRequest registerRequest) {
        //setup object for view
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(REGISTER_OBJECT, new RegisterRequest());

        // implement register for user
        if (userService.checkRegister(registerRequest)) {
            modelAndView.setViewName(AUTH_REDIRECT_LOGIN);
        } else {
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Đăng kí không thành công");
        }

        return modelAndView;
    }

    @GetMapping("/forgotPassword")
    public ModelAndView indexForgotPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("auth/verificationByEmail");
        modelAndView.addObject("email", "");
        return modelAndView;
    }

    @PostMapping("/sendToEmail")
    public ModelAndView doSendCodeToEmail(@ModelAttribute("email") String email) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/auth/checkEmail");

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());

        if (!emailService.sendEmail(verification)) {
            modelAndView.setViewName("/auth/verificationByEmail");
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Gửi mail không thành công!");
        }

        return modelAndView;
    }

    @GetMapping("/verify")
    public ModelAndView doVerify(@RequestParam("code") String code) {
        ModelAndView modelAndView = new ModelAndView();

        if (!emailService.checkCode(code)) {
            modelAndView.setViewName("/auth/verificationByEmail");
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Gửi mail không thành công!");
            return modelAndView;
        }

        String email = emailService.getEmailFromCode(code);

        modelAndView.setViewName("/auth/changePassword");
        modelAndView.addObject("email", email);
        modelAndView.addObject("newPassword", "");
        return modelAndView;
    }


    @PostMapping("/changePassword")
    public ModelAndView doChangePassword(@ModelAttribute("email") String email,
                                         @ModelAttribute("newPassword") String password) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/auth/login");

        if (!userService.changePassword(email, password)) {
            modelAndView.setViewName("/auth/verificationByEmail");
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Gửi mail không thành công!");
        }

        return modelAndView;
    }

}
