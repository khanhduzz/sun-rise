package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryCreateDto addCategoryDto);

    CategoryResponseDto toCategoryResponseDto(Category category);

    Category updateCategory(@MappingTarget Category category, CategoryUpdateDto categoryUpdateDto);
}
