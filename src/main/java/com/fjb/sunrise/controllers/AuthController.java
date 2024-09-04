package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_REDIRECT_LOGIN;
import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VIEW;
import static com.fjb.sunrise.utils.Constants.ApiConstant.ERROR_MESSAGE;
import static com.fjb.sunrise.utils.Constants.ApiConstant.LOGIN_ATTRIBUTE;
import static com.fjb.sunrise.utils.Constants.ApiConstant.REGISTER_ATTRIBUTE;

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
    public ModelAndView indexR(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_ATTRIBUTE, new LoginRequest());
        modelAndView.addObject(REGISTER_ATTRIBUTE, new RegisterRequest());
        if (error != null) {
            modelAndView.addObject(ERROR_MESSAGE, "Đăng nhập không thành công!");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_ATTRIBUTE, new LoginRequest());
        modelAndView.addObject(REGISTER_ATTRIBUTE, new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@ModelAttribute("register") RegisterRequest registerRequest) {
        //setup object for view
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(AUTH_VIEW);
        modelAndView.addObject(LOGIN_ATTRIBUTE, new LoginRequest());
        modelAndView.addObject(REGISTER_ATTRIBUTE, new RegisterRequest());

        // implement register for user
        if (service.checkRegister(registerRequest)) {
            modelAndView.setViewName(AUTH_REDIRECT_LOGIN);
        } else {
            modelAndView.addObject(ERROR_MESSAGE, "Đăng kí không thành công");
        }

        return modelAndView;
    }

}
