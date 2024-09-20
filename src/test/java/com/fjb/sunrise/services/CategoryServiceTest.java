package com.fjb.sunrise.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
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

    private CategoryCreateDto categoryCreateDto;

    private CategoryUpdateDto categoryUpdateDto;


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

        categoryCreateDto = new CategoryCreateDto();
        categoryCreateDto.setName("Category-Test");

        categoryUpdateDto = new CategoryUpdateDto();
        categoryUpdateDto.setId(1L);
        categoryUpdateDto.setName("Category-Test");
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

    @Nested
    class CreateCategoryTests {
        @Test
        void createCategory_shouldReturnCategoryResponseDto() {
            // Giả lập chuyển đổi từ DTO sang Category
            when(categoryMapper.toCategory(categoryCreateDto)).thenReturn(category);
            // Giả lập lưu Category vào repository
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            // Giả lập chuyển đổi từ Category sang CategoryResponseDto
            when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

            // Thực hiện gọi phương thức
            CategoryResponseDto result = categoryService.createCategory(categoryCreateDto);

            // Kiểm tra kết quả
            assertEquals(categoryResponseDto.getName(), result.getName());
            verify(categoryRepository).save(any(Category.class));
        }
    }



    @Nested
    class UpdateCategoryTests {
        @Test
        void updateCategory_shouldReturnUpdatedCategoryResponseDto() {
            Long categoryId = 1L;

            // Giả lập dữ liệu ban đầu
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.updateCategory(category, categoryUpdateDto)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

            // Thực hiện gọi phương thức
            CategoryResponseDto result = categoryService.updateCategory(categoryId, categoryUpdateDto);

            // Kiểm tra kết quả
            assertEquals(categoryResponseDto.getName(), result.getName());
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).save(category);
        }

        @Test
        void updateCategory_shouldThrowNotFound_whenCategoryDoesNotExist() {
            Long categoryId = 1L;

            // Giả lập không tìm thấy danh mục
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Kiểm tra ngoại lệ
            assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryUpdateDto));
        }
    }



    @Nested
    class DisableCategoryTests {
        @Test
        void disableCategory_shouldSetStatusToNotActive() {
            Long categoryId = 1L;

            // Giả lập tìm thấy danh mục
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // Thực hiện gọi phương thức
            categoryService.disableCategory(categoryId);

            // Kiểm tra trạng thái
            assertEquals(EStatus.NOT_ACTIVE, category.getStatus());
            verify(categoryRepository).save(category);
        }

        @Test
        void disableCategory_shouldDoNothing_whenCategoryNotFound() {
            Long categoryId = 1L;

            // Giả lập không tìm thấy danh mục
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Thực hiện gọi phương thức
            categoryService.disableCategory(categoryId);

            // Kiểm tra không có hành động nào được thực hiện
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }


    @Nested
    class EnableCategoryTests {
        @Test
        void enableCategory_shouldSetStatusToActive() {
            Long categoryId = 1L;

            // Giả lập tìm thấy danh mục
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // Thực hiện gọi phương thức
            categoryService.enableCategory(categoryId);

            // Kiểm tra trạng thái
            assertEquals(EStatus.ACTIVE, category.getStatus());
            verify(categoryRepository).save(category);
        }

        @Test
        void enableCategory_shouldDoNothing_whenCategoryNotFound() {
            Long categoryId = 1L;

            // Giả lập không tìm thấy danh mục
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Thực hiện gọi phương thức
            categoryService.enableCategory(categoryId);

            // Kiểm tra không có hành động nào được thực hiện
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

}
