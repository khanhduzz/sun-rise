package com.fjb.sunrise.services;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryStatusDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.models.Category;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;


public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto);

    CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);

    @Transactional
    CategoryResponseDto saveStatusCategory(Long id, CategoryStatusDto categoryStatusDto);

    void disableCategory(Long id);

    void enableCategory(Long id);

    CategoryResponseDto getCategoryById(Long id);

    List<CategoryResponseDto> getAllCategories();

    Page<Category> getCategoryList(DataTableInputDTO payload);

    List<Category> findCategoryByAdminAndUser();
}
