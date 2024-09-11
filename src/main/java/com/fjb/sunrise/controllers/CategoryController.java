package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryStatusDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryFullPageResponse;
import com.fjb.sunrise.dtos.responses.TransactionFullPageResponse;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.Transaction;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.addObject("categoryCreate", new CategoryCreateDto());
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    @PostMapping("/page")
    @ResponseBody
    public CategoryFullPageResponse getPage(@RequestBody DataTableInputDTO payload) {
        Page<Category> categoryPage = categoryService.getCategoryList(payload);
        CategoryFullPageResponse response = new CategoryFullPageResponse();
        response.setData(categoryMapper.listCategoryToListCategoryPageResponse(
                categoryPage.stream().toList()
        ));
        response.setDraw(payload.getDraw());
        response.setRecordsFiltered(categoryPage.getTotalElements());
        response.setRecordsTotal(categoryPage.getTotalElements());
        return response;
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

    @GetMapping("/{id}")
    public ModelAndView getCategory(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("category", categoryService.getCategoryById(id));
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    @PostMapping("/update/{id}")
    public ModelAndView updateCategory(@PathVariable("id") Long id, @ModelAttribute("categoryUpdate")
        @Valid CategoryUpdateDto categoryUpdateDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        if (result.hasErrors()) {
            modelAndView.addObject("category", categoryService.getCategoryById(id));
            return modelAndView;
        }
        categoryService.updateCategory(id, categoryUpdateDto);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    //change-status

    @PostMapping("/delete/{id}")
    public ModelAndView changeStatusCategory(@PathVariable("id") Long id, @ModelAttribute @Valid CategoryStatusDto active) {
        ModelAndView modelAndView = new ModelAndView();
        if (active.isActive()) {
            categoryService.enableCategory(id);
        } else {
            categoryService.disableCategory(id);
        }
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    //get-list

    @GetMapping("/all")
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }
}
