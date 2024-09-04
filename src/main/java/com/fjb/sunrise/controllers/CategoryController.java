package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.utils.Constants;
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

    @GetMapping
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    //create

    @PostMapping("/add")
    public ModelAndView addCategory(@ModelAttribute("categoryCreate")
                                    @Valid CategoryCreateDto categoryCreateDto,
                                    BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        if (result.hasErrors()) {
            return modelAndView;
        }
        categoryService.createCategory(categoryCreateDto);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addCategory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categoryCreate", new CategoryCreateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    //update

    @PostMapping("/{id}")
    public ModelAndView updateCategory(@PathVariable("id") Long id, @ModelAttribute("categoryUpdate")
        @Valid CategoryUpdateDto categoryUpdateDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        if (result.hasErrors()) {
            return modelAndView;
        }
        categoryService.updateCategory(id, categoryUpdateDto);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    //change-status

    @PostMapping("/delete/{id}")
    public ModelAndView changeStatusCategory(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        categoryService.disableCategory(id);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    //get-list

    @GetMapping("/all")
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("category", categoryService.getAllCategories());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }
}
