
package com.examteam.exam_system.attempt.controller;

import com.examteam.exam_system.attempt.dto.AnswerRequest;
import com.examteam.exam_system.attempt.dto.AttemptQuestionResponse;
import com.examteam.exam_system.attempt.dto.AttemptResponse;
import com.examteam.exam_system.attempt.service.ExamAttemptService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@PreAuthorize("hasRole('STUDENT')")
public class ExamAttemptController {

    private final ExamAttemptService attemptService;

    public ExamAttemptController(
            ExamAttemptService attemptService
    ) {
        this.attemptService = attemptService;
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<AttemptResponse> startAttempt(
            @PathVariable Long examId,
            Authentication authentication
    ) {
        AttemptResponse response = attemptService.startAttempt(
                examId,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{attemptId}/questions")
    public ResponseEntity<List<AttemptQuestionResponse>>
    getAttemptQuestions(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                attemptService.getAttemptQuestions(
                        attemptId,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{attemptId}/answers")
    public ResponseEntity<Void> saveAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody AnswerRequest request,
            Authentication authentication
    ) {
        attemptService.saveAnswer(
                attemptId,
                request,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AttemptResponse> submitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                attemptService.submitAttempt(
                        attemptId,
                        authentication.getName()
                )
        );
    }
}
