package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final String CATEGORY_INDEX = "category/index";
    private final String CATEGORY_REDIRECT = "redirect:/category";
    @GetMapping
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.setViewName(CATEGORY_INDEX);
        return modelAndView;
    }

    //create

    @PostMapping("/add")
    public ModelAndView addCategory(@ModelAttribute("categoryCreate")
                                    @Valid CategoryCreateDto categoryCreateDto,
                                    BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CATEGORY_INDEX);
        categoryService.createCategory(categoryCreateDto);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addCategory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categoryCreate", new CategoryCreateDto());
        modelAndView.setViewName(CATEGORY_INDEX);
        return modelAndView;
    }

    //update

    @PostMapping("/{id}")
    public ModelAndView updateCategory(@PathVariable("id") Long id, @ModelAttribute("categoryUpdate")
        @Valid CategoryUpdateDto categoryUpdateDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(CATEGORY_INDEX);
        categoryService.updateCategory(id, categoryUpdateDto);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }

    //change-status

    @PostMapping("/delete/{id}")
    public ModelAndView changeStatusCategory(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        categoryService.changeStatusCategory(id);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }

    //get-list

    @GetMapping("/all")
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("category", categoryService.getAllCategories());
        modelAndView.setViewName(CATEGORY_INDEX);
        return modelAndView;
    }
}
