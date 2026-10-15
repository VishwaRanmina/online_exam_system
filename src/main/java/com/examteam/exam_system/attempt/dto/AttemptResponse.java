
package com.examteam.exam_system.attempt.dto;

import com.examteam.exam_system.attempt.model.AttemptStatus;

import java.time.LocalDateTime;

public record AttemptResponse(
        Long id,
        Long examId,
        Long studentId,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        AttemptStatus status
) {
}
