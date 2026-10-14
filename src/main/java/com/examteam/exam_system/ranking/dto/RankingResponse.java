
package com.examteam.exam_system.ranking.dto;

public record RankingResponse(
        int rank,
        Long studentId,
        String studentNumber,
        String studentName,
        int totalScore,
        int maximumScore,
        double percentage
) {
}
