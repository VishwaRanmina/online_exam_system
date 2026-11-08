
package com.examteam.exam_system.exam.repository;

import com.examteam.exam_system.exam.model.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionRepository
        extends JpaRepository<ExamQuestion, Long> {

    List<ExamQuestion> findByExamId(Long examId);

    boolean existsByExamIdAndQuestionId(
            Long examId,
            Long questionId
    );

    boolean existsByQuestionId(Long questionId);

    long countByExamId(Long examId);
}
