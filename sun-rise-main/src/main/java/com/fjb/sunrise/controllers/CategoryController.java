package com.fjb.sunrise.controllers;

import static com.fjb.sunrise.utils.Constants.ApiConstant.CATEGORY_INDEX;
import static com.fjb.sunrise.utils.Constants.ApiConstant.CATEGORY_REDIRECT;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategorySearchDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        modelAndView.addObject("categoryCreate", new CategoryCreateDto()); // Add DTO for creating category
        modelAndView.addObject("categorySearch", new CategorySearchDto()); // Add DTO for searching
        modelAndView.setViewName(CATEGORY_INDEX);
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

    @PostMapping("/add")
    public ModelAndView addCategory(@ModelAttribute("categoryCreate")
                                    @Valid CategoryCreateDto categoryCreateDto,
                                    BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors()) {
            modelAndView.setViewName(CATEGORY_INDEX);
            return modelAndView;
        }
        categoryService.createCategory(categoryCreateDto);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView updateCategory(@PathVariable("id") Long id, @ModelAttribute("categoryUpdate")
    @Valid CategoryUpdateDto categoryUpdateDto, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors()) {
            modelAndView.setViewName(CATEGORY_INDEX);
            return modelAndView;
        }
        categoryService.updateCategory(id, categoryUpdateDto);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    public ModelAndView changeStatusCategory(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        categoryService.disableCategory(id);
        modelAndView.setViewName(CATEGORY_REDIRECT);
        return modelAndView;
    }
}
