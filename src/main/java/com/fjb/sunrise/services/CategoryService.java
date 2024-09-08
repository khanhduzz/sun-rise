package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto);

    CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);

    void disableCategory(Long id);

    CategoryResponseDto getCategory(Long id);

    List<CategoryResponseDto> getAllCategories();
}
