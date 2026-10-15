
package com.examteam.exam_system.attempt.model;

import com.examteam.exam_system.question.model.Question;
import com.examteam.exam_system.question.model.QuestionOption;

import jakarta.persistence.*;

@Entity
@Table(
        name = "student_answers",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"attempt_id", "question_id"}
        )
)
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    public StudentAnswer() {
    }

    public StudentAnswer(
            ExamAttempt attempt,
            Question question,
            QuestionOption selectedOption
    ) {
        this.attempt = attempt;
        this.question = question;
        this.selectedOption = selectedOption;
    }

    public Long getId() {
        return id;
    }

    public ExamAttempt getAttempt() {
        return attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public QuestionOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(QuestionOption selectedOption) {
        this.selectedOption = selectedOption;
    }
}
