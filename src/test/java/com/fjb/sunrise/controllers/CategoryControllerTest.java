package com.fjb.sunrise.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    // Using mockMvc to get the behavior
    @Autowired
    private MockMvc mockMvc;


    // This test is go to the category/index and assume we see the "Danh sách danh mục"
//    @Test
//    @WithMockUser(username = "testUser", roles = {"ADMIN", "USER"})
//    void goCategoryIndex_shouldGetTheCategoriesInformation() throws Exception {
//        this.mockMvc.perform(get("/category/index"))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(content().string(Matchers.containsString("Danh mục thu/chi")));
//    }

    // This test is assumed that, the user did not log in, so redirect to login page
    @Test
    void goCategoryIndex_shouldRedirectToLogin_ifNotAuthenticated() throws Exception {
        this.mockMvc.perform(get("/sun/category/index"))
            .andDo(print())
            .andExpect(status().is3xxRedirection());
    }
}
