package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_REDIRECT_LOGIN;
import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VERIFICATION;
import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VERIFICATION_CODE;
import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VIEW;
import static com.fjb.sunrise.utils.Constants.ApiConstant.CODE_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.EMAIL_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.ERROR_MESSAGE_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.LOGIN_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.PHONE_OBJECT;
import static com.fjb.sunrise.utils.Constants.ApiConstant.REGISTER_OBJECT;

import com.fjb.sunrise.dtos.requests.LoginRequest;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.services.UserService;
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
    private final UserService service;

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

    @GetMapping("/forgotPassword")
    public ModelAndView indexForgotPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VERIFICATION);
        modelAndView.addObject(EMAIL_OBJECT, "");
        modelAndView.addObject(PHONE_OBJECT, "");
        return modelAndView;
    }

    @GetMapping("/inputCode")
    public ModelAndView indexInputCode() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VERIFICATION_CODE);
        modelAndView.addObject(CODE_OBJECT, "");
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
        if (service.checkRegister(registerRequest)) {
            modelAndView.setViewName(AUTH_REDIRECT_LOGIN);
        } else {
            modelAndView.addObject(ERROR_MESSAGE_OBJECT, "Đăng kí không thành công");
        }

        return modelAndView;
    }

    //this function is prepared in advance and will be rewritten later.
    @PostMapping("/sendToEmail")
    public ModelAndView doSendCodeToEmail(@ModelAttribute(EMAIL_OBJECT) String email) {
        return new ModelAndView();
    }

    //this function is prepared in advance and will be rewritten later.
    @PostMapping("/sendToPhone")
    public ModelAndView doSendCodeToPhone(@ModelAttribute(PHONE_OBJECT) String phone) {
        return new ModelAndView();
    }

    //this function is prepared in advance and will be rewritten later.
    @PostMapping("/sendCode")
    public ModelAndView doVerifyCode(@ModelAttribute(PHONE_OBJECT) String phone) {
        return new ModelAndView();
    }
}
