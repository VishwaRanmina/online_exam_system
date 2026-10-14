
package com.examteam.exam_system.question.controller;

import com.examteam.exam_system.question.dto.QuestionRequest;
import com.examteam.exam_system.question.dto.QuestionResponse;
import com.examteam.exam_system.question.dto.StaffQuestionResponse;
import com.examteam.exam_system.question.service.QuestionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request
    ) {
        QuestionResponse response =
                questionService.createQuestion(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {

        return ResponseEntity.ok(
                questionService.getAllQuestions()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffQuestionResponse> getQuestionById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                questionService.getStaffQuestionById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request
    ) {
        return ResponseEntity.ok(
                questionService.updateQuestion(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id
    ) {
        questionService.deleteQuestion(id);

        return ResponseEntity.noContent().build();
    }

}
