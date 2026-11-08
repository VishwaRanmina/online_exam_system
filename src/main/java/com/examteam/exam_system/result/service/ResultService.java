
package com.examteam.exam_system.result.service;

import com.examteam.exam_system.attempt.model.AttemptStatus;
import com.examteam.exam_system.attempt.model.ExamAttempt;
import com.examteam.exam_system.attempt.model.StudentAnswer;
import com.examteam.exam_system.attempt.repository.ExamAttemptRepository;
import com.examteam.exam_system.attempt.repository.StudentAnswerRepository;

import com.examteam.exam_system.exam.model.ExamQuestion;
import com.examteam.exam_system.exam.repository.ExamQuestionRepository;

import com.examteam.exam_system.question.model.Question;
import com.examteam.exam_system.question.model.QuestionOption;

import com.examteam.exam_system.result.calculation.ScoreCalculator;
import com.examteam.exam_system.result.dto.ResultResponse;
import com.examteam.exam_system.result.model.Result;
import com.examteam.exam_system.result.repository.ResultRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private final ResultRepository resultRepository;
    private final ExamAttemptRepository attemptRepository;
    private final StudentAnswerRepository answerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public ResultService(
            ResultRepository resultRepository,
            ExamAttemptRepository attemptRepository,
            StudentAnswerRepository answerRepository,
            ExamQuestionRepository examQuestionRepository
    ) {
        this.resultRepository = resultRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    @Transactional
    public ResultResponse calculateResult(Long attemptId) {

        ExamAttempt attempt = findAttempt(attemptId);

        if (attempt.getStatus() != AttemptStatus.SUBMITTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam attempt has not been submitted"
            );
        }

        if (resultRepository.existsByAttemptId(attemptId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Result already calculated"
            );
        }

        List<ExamQuestion> examQuestions =
                examQuestionRepository.findByExamId(
                        attempt.getExam().getId()
                );

        if (examQuestions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam has no questions"
            );
        }

        List<StudentAnswer> studentAnswers =
                answerRepository.findByAttemptId(attemptId);

        Map<Long, StudentAnswer> answersByQuestion =
                studentAnswers.stream().collect(
                        Collectors.toMap(
                                answer -> answer.getQuestion().getId(),
                                Function.identity()
                        )
                );

        List<Long> selectedIds = new ArrayList<>();
        List<Long> correctIds = new ArrayList<>();
        List<Integer> marks = new ArrayList<>();

        int maximumScore = 0;

        for (ExamQuestion examQuestion : examQuestions) {

            Question question = examQuestion.getQuestion();

            Long correctOptionId = question.getOptions()
                    .stream()
                    .filter(QuestionOption::isCorrect)
                    .map(QuestionOption::getId)
                    .findFirst()
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.CONFLICT,
                                    "Question has no correct option"
                            )
                    );

            StudentAnswer answer =
                    answersByQuestion.get(question.getId());

            Long selectedOptionId = null;

            if (answer != null &&
                    answer.getSelectedOption() != null) {

                selectedOptionId =
                        answer.getSelectedOption().getId();
            }

            selectedIds.add(selectedOptionId);
            correctIds.add(correctOptionId);
            marks.add(question.getMarks());

            maximumScore = Math.addExact(
                    maximumScore,
                    question.getMarks()
            );
        }

        int totalScore = ScoreCalculator.calculateScore(
                selectedIds,
                correctIds,
                marks
        );

        Result result = new Result(
                attempt,
                totalScore,
                maximumScore
        );

        return toResponse(resultRepository.save(result));
    }

    @Transactional
    public ResultResponse releaseResult(Long resultId) {

        Result result = findResult(resultId);

        if (result.isReleased()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Result has already been released"
            );
        }

        result.release();

        return toResponse(resultRepository.save(result));
    }

    @Transactional(readOnly = true)
    public ResultResponse getResultById(Long resultId) {
        return toResponse(findResult(resultId));
    }

    @Transactional(readOnly = true)
    public List<ResultResponse> getResultsByExam(Long examId) {

        return resultRepository.findByAttemptExamId(examId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultResponse> getMyReleasedResults(
            String authenticatedEmail
    ) {

        return resultRepository.findByReleasedTrue()
                .stream()
                .filter(result ->
                        result.getAttempt()
                                .getStudent()
                                .isActive()
                                &&
                                result.getAttempt()
                                        .getStudent()
                                        .getEmail()
                                        .equalsIgnoreCase(authenticatedEmail)
                )
                .map(this::toResponse)
                .toList();
    }

    private ExamAttempt findAttempt(Long attemptId) {

        return attemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Attempt not found: " + attemptId
                        )
                );
    }

    private Result findResult(Long resultId) {

        return resultRepository.findById(resultId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Result not found: " + resultId
                        )
                );
    }

    private ResultResponse toResponse(Result result) {

        ExamAttempt attempt = result.getAttempt();

        return new ResultResponse(
                result.getId(),
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getStudent().getId(),
                result.getTotalScore(),
                result.getMaximumScore(),
                result.isReleased(),
                result.getCalculatedAt()
        );
    }
}
