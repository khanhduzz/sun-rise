package com.fjb.sunrise.repositories;

import com.fjb.sunrise.enums.ERole;
import com.fjb.sunrise.models.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User findByUsername(String username);

    @Query("FROM User u WHERE u.email = :username OR u.phone = :username")
    User findByEmailOrPhone(@Param("username") String username);

    boolean existsUserByEmailOrPhone(String email, String phone);

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    List<User> findAllByRole(ERole role);

}
