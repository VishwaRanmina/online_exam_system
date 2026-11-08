
package com.examteam.exam_system.result.controller;

import com.examteam.exam_system.result.dto.ResultResponse;
import com.examteam.exam_system.result.service.ResultService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    // Calculate marks for a submitted attempt
    @PostMapping("/attempts/{attemptId}/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ResultResponse> calculateResult(
            @PathVariable Long attemptId
    ) {
        return ResponseEntity.ok(
                resultService.calculateResult(attemptId)
        );
    }

    // Release a calculated result
    @PostMapping("/{resultId}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ResultResponse> releaseResult(
            @PathVariable Long resultId
    ) {
        return ResponseEntity.ok(
                resultService.releaseResult(resultId)
        );
    }

    // Get a result by ID (staff only)
    @GetMapping("/{resultId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<ResultResponse> getResultById(
            @PathVariable Long resultId
    ) {
        return ResponseEntity.ok(
                resultService.getResultById(resultId)
        );
    }

    // Get all results belonging to an exam (staff only)
    @GetMapping("/exams/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<List<ResultResponse>> getResultsByExam(
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                resultService.getResultsByExam(examId)
        );
    }

    // Student can view only their released results
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ResultResponse>> getMyResults(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                resultService.getMyReleasedResults(
                        authentication.getName()
                )
        );
    }
}
