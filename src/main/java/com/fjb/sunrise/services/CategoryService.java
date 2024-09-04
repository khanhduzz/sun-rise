package com.fjb.sunrise.services;


import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.TransactionType;
import com.fjb.sunrise.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> getCategoriesByType(TransactionType type) {
        return categoryRepository.findByType(type);
    }
    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}
