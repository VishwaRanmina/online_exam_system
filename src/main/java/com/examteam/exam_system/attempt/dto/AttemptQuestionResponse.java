
package com.examteam.exam_system.attempt.dto;

import java.util.List;

public record AttemptQuestionResponse(
        Long questionId,
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
