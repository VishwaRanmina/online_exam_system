
package com.examteam.exam_system.exam.dto;

import com.examteam.exam_system.exam.model.ExamStatus;

import java.time.LocalDateTime;

public record ExamResponse(
        Long id,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int durationMinutes,
        ExamStatus status
) {
}
