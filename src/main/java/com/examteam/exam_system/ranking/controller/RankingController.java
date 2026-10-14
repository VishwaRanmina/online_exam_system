
package com.examteam.exam_system.ranking.controller;

import com.examteam.exam_system.ranking.dto.ExamReportResponse;
import com.examteam.exam_system.ranking.dto.RankingResponse;
import com.examteam.exam_system.ranking.service.RankingService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    // Get leaderboard for an exam
    @GetMapping("/exams/{examId}")
    public ResponseEntity<List<RankingResponse>> getExamRankings(
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                rankingService.getExamRankings(examId)
        );
    }

    // Get performance statistics for an exam
    @GetMapping("/exams/{examId}/report")
    public ResponseEntity<ExamReportResponse> getExamReport(
            @PathVariable Long examId
    ) {
        return ResponseEntity.ok(
                rankingService.getExamReport(examId)
        );
    }
}
