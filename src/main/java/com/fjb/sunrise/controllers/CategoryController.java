package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryFullPageResponse;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    private static final String CATEGORY_CREATE = "categoryCreate";

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("categories", categoryService.getAllCategories());
        modelAndView.addObject(CATEGORY_CREATE, new CategoryCreateDto());
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    @PostMapping("/page")
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
    public ModelAndView addCategory(@ModelAttribute(CATEGORY_CREATE)
                                    @Valid CategoryCreateDto categoryCreateDto,
                                    BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        if (result.hasErrors()) {
            modelAndView.addObject(CATEGORY_CREATE, new CategoryCreateDto());
            return modelAndView;
        }
        categoryService.createCategory(categoryCreateDto);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView addCategory() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(CATEGORY_CREATE, new CategoryCreateDto());
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_INDEX);
        return modelAndView;
    }

    //update

    @GetMapping("/{id}")
    public ModelAndView getCategory(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("category", categoryService.getCategoryById(id));
        modelAndView.addObject("categoryUpdate", new CategoryUpdateDto());
        modelAndView.addObject(CATEGORY_CREATE, new CategoryCreateDto());
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
            modelAndView.addObject(CATEGORY_CREATE, new CategoryCreateDto());
            return modelAndView;
        }
        categoryService.updateCategory(id, categoryUpdateDto);
        modelAndView.setViewName(Constants.ApiConstant.CATEGORY_REDIRECT);
        return modelAndView;
    }

    //change-status

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deactivate/{id}")
    public String disableCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.disableCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category deactivated successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute(Constants.ErrorCode.ERROR, e.getMessage());
        }
        return Constants.ApiConstant.CATEGORY_INDEX;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/activate/{id}")
    public String enableCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.enableCategory(id);
            redirectAttributes.addFlashAttribute("message", "Category activated successfully");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return Constants.ApiConstant.CATEGORY_INDEX;
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
