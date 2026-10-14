
package com.examteam.exam_system.exam.repository;

import com.examteam.exam_system.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository
        extends JpaRepository<Exam, Long> {
}
