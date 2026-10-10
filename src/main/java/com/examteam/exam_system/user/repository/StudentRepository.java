package com.examteam.exam_system.user.repository;

import com.examteam.exam_system.user.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByStudentNumberAndIdNot(
            String studentNumber, Long id);

    List<Student> findByActiveTrue();

    Optional<Student> findByIdAndActiveTrue(Long id);

}
