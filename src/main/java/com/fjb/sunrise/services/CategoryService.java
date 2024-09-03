package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.request.CategoryCreateDto;
import com.fjb.sunrise.dtos.request.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto);

    CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);

    void changeStatusCategory(Long id);

    List<CategoryResponseDto> getAllCategories();
}
