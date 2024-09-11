package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryStatusDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryFullPageResponse;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.services.CategoryService;
import com.fjb.sunrise.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView changeStatusCategory(@PathVariable("id") Long id, @ModelAttribute("categoryStatus")
                                                @Valid CategoryStatusDto categoryStatusDto,
                                             RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        CategoryResponseDto category = categoryService.getCategoryById(id);
        try {
            if (category.getStatus() == EStatus.NOT_ACTIVE) {
                categoryService.enableCategory(id);
                redirectAttributes.addFlashAttribute("message", "Category enabled successfully.");
            } else if (category.getStatus() == EStatus.ACTIVE) {
                categoryService.disableCategory(id);
                redirectAttributes.addFlashAttribute("message", "Category disabled successfully.");
            }
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        categoryService.saveStatusCategory(id, categoryStatusDto);
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
