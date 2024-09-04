package com.fjb.sunrise.repositories;

import com.example.demo.model.Category;
import com.example.demo.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(TransactionType type);
    List<Category> findByNameContainingIgnoreCase(String name);
}
