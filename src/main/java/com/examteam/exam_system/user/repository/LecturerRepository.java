package com.examteam.exam_system.user.repository;

import com.examteam.exam_system.user.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LecturerRepository
        extends JpaRepository<Lecturer, Long> {

    boolean existsByEmployeeNumber(String employeeNumber);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumberAndIdNot(
            String employeeNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Lecturer> findByActiveTrue();

    Optional<Lecturer> findByIdAndActiveTrue(Long id);

    long countByActiveTrue();
}