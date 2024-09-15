package com.fjb.sunrise.services.impl;

import com.fjb.sunrise.dtos.base.DataTableInputDTO;
import com.fjb.sunrise.dtos.requests.CategoryCreateDto;
import com.fjb.sunrise.dtos.requests.CategoryUpdateDto;
import com.fjb.sunrise.dtos.responses.CategoryResponseDto;
import com.fjb.sunrise.mappers.CategoryMapper;
import com.fjb.sunrise.models.Category;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.CategoryRepository;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.services.CategoryService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto) {
        Category category = categoryMapper.toCategory(categoryCreateDto);
        category.setActive(true);
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
            x.setActive(false);
            categoryRepository.save(x);
        });
    }

    @Override
    public void enableCategory(Long id) {
        categoryRepository.findById(id).ifPresent(x -> {
            x.setActive(true);
            categoryRepository.save(x);
        });
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::toCategoryResponseDto)
            .orElseThrow();
    }

    @Override
    public Page<Category> getCategoryList(DataTableInputDTO payload) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("owner.role"));

        if (!payload.getOrder().isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.fromString(payload.getOrder().get(0).get("dir").toUpperCase()),
                payload.getOrder().get(0).get("colName")));
        }

        Sort sortOpt = Sort.by(orders);
        int pageNumber = payload.getStart() / 10;
        if (payload.getStart() % 10 != 0) {
            pageNumber = pageNumber - 1;
        }

        Pageable pageable = PageRequest.of(pageNumber, payload.getLength(), sortOpt);

        Specification<Category> specs = findAllByUser();
        return categoryRepository.findAll(specs, pageable);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
            .stream().map(categoryMapper::toCategoryResponseDto)
            .toList();
    }

    @Override
    public List<Category> findCategoryByAdminAndUser() {
        Specification<Category> specs = findAllByUser();
        return categoryRepository.findAll(specs);
    }
    private Specification<Category> findAllByUser() {

//        specs = specs.or((root, query, builder) -> {
//                Join<Category, User> userJoin = root.join("owner");
//                return builder.equal(userJoin.get("id"), getCurrentUserId());
//            }
//        );

        return Specification.where((root, query, builder) -> {
                Join<Category, User> userJoin = root.join("owner");
                Predicate hasRoleAdmin = builder.equal(userJoin.get("role"), "ADMIN");
                Predicate isOwner = builder.equal(userJoin.get("id"), getCurrentUserId());

                return builder.or(hasRoleAdmin, isOwner);
            }
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user =
            (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User dbUser = userRepository.findByEmailOrPhone(user.getUsername());
        return dbUser.getId();
    }

}
