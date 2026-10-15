
package com.examteam.exam_system.attempt.repository;

import com.examteam.exam_system.attempt.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswer, Long> {

    // Get all answers belonging to an attempt
    List<StudentAnswer> findByAttemptId(Long attemptId);

    // Find an existing answer for a particular question
    Optional<StudentAnswer> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    // Check whether a question has an answer record
    boolean existsByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    // Count saved answer records
    long countByAttemptId(Long attemptId);
}
