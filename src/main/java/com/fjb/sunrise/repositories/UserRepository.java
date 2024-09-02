package com.fjb.sunrise.repositories;

import com.fjb.sunrise.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User findByUsername(String username);

    @Query("FROM User u WHERE u.email = :username OR u.phone = :username")
    User findByEmailOrPhone(@Param("username") String username);

    boolean existsUserByEmailOrPhone(String email, String phone);
}
