
package com.examteam.exam_system.question.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        int marks,
        List<OptionResponse> options
) {
    public record OptionResponse(
            Long id,
            String text
    ) {
    }
}
