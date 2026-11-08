
package com.examteam.exam_system.exam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExamRequest(

        @NotBlank
        String title,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        @Min(1)
        int durationMinutes
) {
}
