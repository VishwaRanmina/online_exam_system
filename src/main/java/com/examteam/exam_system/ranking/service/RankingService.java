package com.examteam.exam_system.ranking.service;

import com.examteam.exam_system.exam.model.Exam;
import com.examteam.exam_system.exam.repository.ExamRepository;

import com.examteam.exam_system.ranking.algorithm.SelectionSortRanking;
import com.examteam.exam_system.ranking.dto.ExamReportResponse;
import com.examteam.exam_system.ranking.dto.RankingResponse;

import com.examteam.exam_system.result.model.Result;
import com.examteam.exam_system.result.repository.ResultRepository;

import com.examteam.exam_system.user.model.Student;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankingService {

    private final ResultRepository resultRepository;
    private final ExamRepository examRepository;

    public RankingService(
            ResultRepository resultRepository,
            ExamRepository examRepository
    ) {
        this.resultRepository = resultRepository;
        this.examRepository = examRepository;
    }

    @Transactional(readOnly = true)
    public List<RankingResponse> getExamRankings(Long examId) {

        findExam(examId);

        List<Result> results = resultRepository
                .findByAttemptExamId(examId)
                .stream()
                .filter(Result::isReleased)
                .toList();

        int n = results.size();

        Long[] studentIds = new Long[n];
        String[] studentNumbers = new String[n];
        int[] scores = new int[n];

        for (int i = 0; i < n; i++) {

            Result result = results.get(i);

            studentIds[i] = student.getId();
            studentNumbers[i] = student.getStudentNumber();
            scores[i] = result.getTotalScore();
        }

        SelectionSortRanking.sort(
                studentIds,
                studentNumbers,
                scores
        );

        List<RankingResponse> rankings = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            Long studentId = studentIds[i];



                    ? 0

            rankings.add(new RankingResponse(
                    i + 1,
                    student.getStudentNumber(),
                    student.getName(),
                    percentage
            ));
        }

        return rankings;
    }

    @Transactional(readOnly = true)
    public ExamReportResponse getExamReport(Long examId) {

        Exam exam = findExam(examId);

        List<Result> results = resultRepository
                .findByAttemptExamId(examId);

        long totalStudents = results.size();

        if (results.isEmpty()) {
            return new ExamReportResponse(
                    examId,
                    exam.getTitle(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        int highestScore = Integer.MIN_VALUE;
        int lowestScore = Integer.MAX_VALUE;

        long passedStudents = 0;
        long totalScore = 0;

        for (Result result : results) {

            int score = result.getTotalScore();

            totalScore += score;

            if (score > highestScore) {
                highestScore = score;
            }

            if (score < lowestScore) {
                lowestScore = score;
            }

                    ? 0

            if (percentage >= 50.0) {
                passedStudents++;
            }
        }

        long failedStudents = totalStudents - passedStudents;

        double averageScore =
                (double) totalScore / totalStudents;

        double passPercentage =
                (passedStudents * 100.0) / totalStudents;

        return new ExamReportResponse(
                examId,
                exam.getTitle(),
                totalStudents,
                averageScore,
                highestScore,
                lowestScore,
                passedStudents,
                failedStudents,
                passPercentage
        );
    }

    private Exam findExam(Long examId) {

        return examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Exam not found: " + examId
                        )
                );
    }
}
