package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategorySearchDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.fjb.sunrise.utils.Constants.ApiConstant.CATEGORY_INDEX;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.addObject("categoryCreate", new CategoryCreateDto());
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
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

    @GetMapping("/{id}")
    public ModelAndView getCategory(@PathVariable("id") Long id, Category category) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("category", categoryService.getCategory(id));
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView updateCategory(@PathVariable("id") Long id, @ModelAttribute("categoryUpdate")
        @Valid CategoryUpdateDto categoryUpdateDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        if (result.hasErrors()) {
            modelAndView.addObject("category", categoryService.getCategory(id));
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
        modelAndView.addObject("getAllCategories", categoryService.getAllCategories());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    // Search Categories
    @PostMapping("/search")
    public ModelAndView searchCategories(@ModelAttribute("categorySearch") CategorySearchDto searchDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.searchCategories(searchDto));
        modelAndView.addObject("categoryCreate", new CategoryCreateDto());
        modelAndView.setViewName(CATEGORY_INDEX);
        return modelAndView;
    }

    @GetMapping("/list")
    public ModelAndView listCategories(@ModelAttribute("categorySearch") CategorySearchDto searchDto,
                                       @RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 3); // Page size is 3
        Page<CategoryResponseDto> pageResult = categoryService.searchCategories(searchDto, pageable);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", pageResult);
        modelAndView.addObject("categoryCreate", new CategoryCreateDto()); // To keep add form
        modelAndView.addObject("categorySearch", searchDto); // Preserve search query
        modelAndView.addObject("currentPage", pageResult.getNumber());
        modelAndView.addObject("totalPages", pageResult.getTotalPages());
        modelAndView.setViewName(CATEGORY_INDEX);
        return modelAndView;
    }
}
