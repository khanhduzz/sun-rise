package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.services.CategoryService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto) {
        Category category = categoryMapper.toCategory(categoryCreateDto);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category = categoryMapper.updateCategory(category, categoryUpdateDto);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(category);
    }

    @Override
    public void disableCategory(Long id) {
        categoryRepository.findById(id).ifPresent(x -> {
            x.setActivate(false);
            categoryRepository.save(x);
        });
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
            .stream().map(categoryMapper::toCategoryResponseDto)
            .toList();
    }
}
