
package com.examteam.exam_system.ranking.service;

import com.examteam.exam_system.exam.model.Exam;
import com.examteam.exam_system.exam.repository.ExamRepository;

import com.examteam.exam_system.ranking.algorithm.SelectionSortRanking;
import com.examteam.exam_system.ranking.datastructure.StudentLinkedList;
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

        // Check whether the exam exists
        findExam(examId);

        // Retrieve released results for this exam
        List<Result> results = resultRepository
                .findByAttemptExamId(examId)
                .stream()
                .filter(Result::isReleased)
                .toList();

        // Create our custom LinkedList
        StudentLinkedList studentList = new StudentLinkedList();

        // Store student records dynamically
        for (Result result : results) {

            Student student = result.getAttempt().getStudent();

            if (studentList.findById(student.getId()) == null) {
                studentList.add(student);
            }
        }

        int n = results.size();

        // Prepare data for Selection Sort
        Long[] studentIds = new Long[n];
        String[] studentNumbers = new String[n];
        int[] scores = new int[n];

        for (int i = 0; i < n; i++) {

            Result result = results.get(i);

            // Retrieve student from our LinkedList
            Student student = studentList.findById(
                    result.getAttempt().getStudent().getId()
            );

            if (student == null) {
                throw new IllegalStateException(
                        "Student not found in LinkedList"
                );
            }

            studentIds[i] = student.getId();
            studentNumbers[i] = student.getStudentNumber();
            scores[i] = result.getTotalScore();
        }

        // Manual Selection Sort
        SelectionSortRanking.sort(
                studentIds,
                studentNumbers,
                scores
        );

        List<RankingResponse> rankings = new ArrayList<>();

        // Build leaderboard in sorted order
        for (int i = 0; i < n; i++) {

            Long studentId = studentIds[i];

            // Retrieve student details from LinkedList
            Student student = studentList.findById(studentId);

            if (student == null) {
                throw new IllegalStateException(
                        "Student not found in LinkedList"
                );
            }

            // Find the result belonging to this student
            Result matchingResult = null;

            for (Result result : results) {

                if (result.getAttempt()
                        .getStudent()
                        .getId()
                        .equals(studentId)) {

                    matchingResult = result;
                    break;
                }
            }

            if (matchingResult == null) {
                throw new IllegalStateException(
                        "Result not found for student: " + studentId
                );
            }

            int totalScore = matchingResult.getTotalScore();
            int maximumScore = matchingResult.getMaximumScore();

            double percentage = maximumScore <= 0
                    ? 0
                    : (totalScore * 100.0) / maximumScore;

            rankings.add(new RankingResponse(
                    i + 1,
                    student.getId(),
                    student.getStudentNumber(),
                    student.getName(),
                    totalScore,
                    maximumScore,
                    percentage
            ));
        }

        return rankings;
    }

    @Transactional(readOnly = true)
    public ExamReportResponse getExamReport(Long examId) {

        Exam exam = findExam(examId);

        // Staff report includes calculated results,
        // even if they have not been released yet.
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

        // Calculate exam statistics
        for (Result result : results) {

            int score = result.getTotalScore();
            int maximumScore = result.getMaximumScore();

            totalScore += score;

            if (score > highestScore) {
                highestScore = score;
            }

            if (score < lowestScore) {
                lowestScore = score;
            }

            double percentage = maximumScore <= 0
                    ? 0
                    : (score * 100.0) / maximumScore;

            // Pass mark is 50%
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
