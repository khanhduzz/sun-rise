package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategorySearchDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto);

    CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);

    void disableCategory(Long id);

    CategoryResponseDto getCategory(Long id);

    List<CategoryResponseDto> getAllCategories();

    Page<CategoryResponseDto> searchCategories(CategorySearchDto searchDto, Pageable pageable);

    List<CategoryResponseDto> searchCategories(CategorySearchDto searchDto);
}
