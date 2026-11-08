
package com.examteam.exam_system.exam.controller;

import com.examteam.exam_system.exam.dto.ExamRequest;
import com.examteam.exam_system.exam.dto.ExamResponse;
import com.examteam.exam_system.exam.service.ExamService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ExamResponse> createExam(
            @Valid @RequestBody ExamRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(examService.createExam(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ExamResponse>> getAllExams(
            Authentication authentication
    ) {
        boolean isStaff = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                                || authority.getAuthority().equals("ROLE_LECTURER")
                );

        if (isStaff) {
            return ResponseEntity.ok(
                    examService.getAllExams()
            );
        }

        return ResponseEntity.ok(
                examService.getPublishedExams()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExamResponse> getExamById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isStaff = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                                || authority.getAuthority().equals("ROLE_LECTURER")
                );

        if (isStaff) {
            return ResponseEntity.ok(
                    examService.getExamById(id)
            );
        }

        return ResponseEntity.ok(
                examService.getPublishedExamById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ExamResponse> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {
        return ResponseEntity.ok(
                examService.updateExam(id, request)
        );
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ExamResponse> publishExam(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                examService.publishExam(id)
        );
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ExamResponse> cancelExam(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                examService.cancelExam(id)
        );
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<Long>> getQuestionIds(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                examService.getQuestionIds(id)
        );
    }

    @PostMapping("/{examId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<Void> assignQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId
    ) {
        examService.assignQuestion(examId, questionId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable Long examId,
            @PathVariable Long questionId
    ) {
        examService.removeQuestion(examId, questionId);

        return ResponseEntity.noContent().build();
    }
}
