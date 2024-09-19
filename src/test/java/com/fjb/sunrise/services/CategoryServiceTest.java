package com.fjb.sunrise.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class CategoryServiceTest {

    // The class to test, this annotation to inject all mock below to this
    @InjectMocks
    private CategoryServiceImpl categoryService;

    // This is mock, declare here to be able to mock it later
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    // Class for re-use in test
    private Category category;
    private CategoryResponseDto categoryResponseDto;


    // Setup this, to re-use code along test cases
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        category = Category.builder()
            .id(1L)
            .name("Category-Test")
            .status(EStatus.ACTIVE)
            .build();

        categoryResponseDto = CategoryResponseDto.builder()
            .id(1L)
            .name("Category-Test")
            .status(EStatus.ACTIVE)
            .build();
    }


    @Nested
    class HappyCase {
        @Test
        void getCategoryById_shouldReturnCategoryResponseDto() {
            // Mock these thing, declare it with the object we want it to return
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

            // Testing area, where we run the function which want to test, then get the result
            CategoryResponseDto result = categoryService.getCategoryById(1L);

            // Check test result, compare the value we faked with the value the function return
            assertEquals(categoryResponseDto.getName(), result.getName());
        }
    }

    @Nested
    class UnHappyCase{
        @Test
        void getCategoryById_shouldReturn404_whenNotFound() {
            // Simulate the repository will return nothing when try to get category
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Then, when we run the service, service will throw exception
            assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1L));
        }
    }
}
