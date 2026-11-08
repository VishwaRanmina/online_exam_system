
package com.examteam.exam_system.ranking.dto;

public record ExamReportResponse(
        Long examId,
        String examTitle,
        long totalStudents,
        double averageScore,
        int highestScore,
        int lowestScore,
        long passedStudents,
        long failedStudents,
        double passPercentage
) {
}
