
package com.examteam.exam_system.result.dto;

import java.time.LocalDateTime;

public record ResultResponse(
        Long id,
        Long attemptId,
        Long examId,
        Long studentId,
        int totalScore,
        int maximumScore,
        boolean released,
        LocalDateTime calculatedAt
) {
}
