package com.fintrack.repository;

import com.fintrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA generates the implementation of this interface at runtime.
 * Just by declaring method names, we get working SQL queries for free.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
