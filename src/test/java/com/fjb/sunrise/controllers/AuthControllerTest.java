package com.fjb.sunrise.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fjb.sunrise.dtos.requests.LoginRequest;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.utils.Constants;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testUser", roles = {"ADMIN", "USER"})
    void testController() throws Exception {
        this.mockMvc.perform(get("/health"))
            .andDo(print())
            .andExpect(status().isOk());
    }

//    @Test
//    void test_login_view() throws Exception {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
//        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
//        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
//
//        this.mockMvc.perform(get("/auth/login")).andExpect(status().isOk()).andReturn().equals(modelAndView);
//    }
//
//    @Test
//    void test_login_view_error() throws Exception {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
//        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
//        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
//        modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, "Đăng nhập không thành công!");
//
//        this.mockMvc.perform(get("/auth/login?error=error")).andExpect(status().isOk()).andReturn().equals(modelAndView);
//    }
//
//    @Test
//    void test_register_view() throws Exception {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
//        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
//        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
//
//        this.mockMvc.perform(get("/auth/register")).andExpect(status().isOk()).andReturn().equals(modelAndView);
//    }
//
//    @Test
//    void test_do_register() throws Exception {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
//        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
//        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
//
//        this.mockMvc.perform(get("/auth/register")).andExpect(status().isOk()).andReturn().equals(modelAndView);
//    }
}
