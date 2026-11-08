package com.examteam.exam_system.user.repository;

import com.examteam.exam_system.user.model.Role;

import com.examteam.exam_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    long countByActiveTrue();

    boolean existsByRole(Role role);

}