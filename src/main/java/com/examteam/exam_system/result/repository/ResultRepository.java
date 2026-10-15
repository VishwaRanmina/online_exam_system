
package com.examteam.exam_system.result.repository;

import com.examteam.exam_system.result.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository
        extends JpaRepository<Result, Long> {

    Optional<Result> findByAttemptId(Long attemptId);

    boolean existsByAttemptId(Long attemptId);

    List<Result> findByAttemptExamId(Long examId);

    List<Result> findByAttemptStudentId(Long studentId);

    List<Result> findByReleasedTrue();
}
