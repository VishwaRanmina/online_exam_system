
package com.examteam.exam_system.result.model;

import com.examteam.exam_system.attempt.model.ExamAttempt;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "results",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "attempt_id"
        )
)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private int maximumScore;

    @Column(nullable = false)
    private boolean released = false;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    public Result() {
    }

    public Result(
            ExamAttempt attempt,
            int totalScore,
            int maximumScore
    ) {
        this.attempt = attempt;
        this.totalScore = totalScore;
        this.maximumScore = maximumScore;
        this.calculatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ExamAttempt getAttempt() {
        return attempt;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getMaximumScore() {
        return maximumScore;
    }

    public boolean isReleased() {
        return released;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void release() {
        this.released = true;
    }
}
