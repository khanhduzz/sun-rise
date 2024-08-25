package com.fjb.sunrise.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ModelAndView health() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("health");
        return modelAndView;
    }
}
