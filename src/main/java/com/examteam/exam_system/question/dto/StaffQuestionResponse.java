
package com.examteam.exam_system.question.dto;

public record StaffQuestionResponse(
        QuestionResponse question,
        Long correctOptionId
) {
}
