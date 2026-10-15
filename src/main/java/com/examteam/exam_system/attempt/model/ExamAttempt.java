
package com.examteam.exam_system.attempt.model;

import com.examteam.exam_system.exam.model.Exam;
import com.examteam.exam_system.user.model.Student;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    public ExamAttempt() {
    }

    public ExamAttempt(Exam exam, Student student) {
        this.exam = exam;
        this.student = student;
        this.startedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Exam getExam() {
        return exam;
    }

    public Student getStudent() {
        return student;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void submit() {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "This attempt has already been submitted"
            );
        }

        status = AttemptStatus.SUBMITTED;
        submittedAt = LocalDateTime.now();
    }
}
