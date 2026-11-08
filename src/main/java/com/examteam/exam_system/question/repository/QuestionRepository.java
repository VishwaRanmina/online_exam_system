
package com.examteam.exam_system.question.repository;

import com.examteam.exam_system.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {
}
