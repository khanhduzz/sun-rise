package com.fjb.sunrise.repositories;

import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Page<Category> findAll(Pageable pageable);

    int countByOwner(User owner);
}
