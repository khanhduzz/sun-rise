package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.requests.LoginRequest;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService service;

    @GetMapping({ "/login", "/register"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginAndRegister");
        modelAndView.addObject("login", new LoginRequest());
        modelAndView.addObject("register", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@ModelAttribute("register") RegisterRequest registerRequest) {
        //setup object for view
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginAndRegister");
        modelAndView.addObject("login", new LoginRequest());
        modelAndView.addObject("register", new RegisterRequest());

        // check null fields
        if (registerRequest.getEmail().isEmpty()
            || registerRequest.getPhone().isEmpty()
            || registerRequest.getPassword().isEmpty()
            || registerRequest.getLastname().isEmpty()
            || registerRequest.getFirstname().isEmpty()
            || registerRequest.getRePassword().isEmpty()) {
            modelAndView.addObject("errorMessage", "Đăng kí không thành công");
            return modelAndView;
        }

        // implement register for user
        if (service.checkRegister(registerRequest)) {
            modelAndView.setViewName("redirect:/auth/login");
        } else {
            modelAndView.addObject("errorMessage", "Đăng kí không thành công");
        }
        return modelAndView;
    }

}
