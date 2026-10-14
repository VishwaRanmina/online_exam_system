
package com.examteam.exam_system.question.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionRequest(

        @NotBlank
        String text,

        @Min(1)
        int marks,

        @NotNull
        @Size(min = 4, max = 4)
        List<@NotBlank String> options,

        @Min(0)
        int correctOptionIndex

) {
}
