
package com.examteam.exam_system.attempt.dto;

import jakarta.validation.constraints.NotNull;

public record AnswerRequest(

        @NotNull
        Long questionId,

        Long selectedOptionId
) {
}
