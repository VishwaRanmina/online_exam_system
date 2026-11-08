
package com.examteam.exam_system.attempt.repository;

import com.examteam.exam_system.attempt.model.ExamAttempt;
import com.examteam.exam_system.attempt.model.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Long> {

    // Find all attempts made by a student
    List<ExamAttempt> findByStudentId(Long studentId);

    // Find attempts belonging to an exam
    List<ExamAttempt> findByExamId(Long examId);

    // Check whether a student has attempted an exam
    boolean existsByExamIdAndStudentId(
            Long examId,
            Long studentId
    );

    // Find a student's attempt for an exam
    Optional<ExamAttempt> findByExamIdAndStudentId(
            Long examId,
            Long studentId
    );

    // Find attempts by their current status
    List<ExamAttempt> findByStatus(AttemptStatus status);
}
