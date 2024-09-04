package com.fjb.sunrise.repositories;


import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(TransactionType type);
    List<Category> findByNameContainingIgnoreCase(String name);
}
