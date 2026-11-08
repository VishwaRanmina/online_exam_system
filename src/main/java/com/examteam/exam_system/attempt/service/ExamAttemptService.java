
package com.examteam.exam_system.attempt.service;

import com.examteam.exam_system.attempt.dto.AnswerRequest;
import com.examteam.exam_system.attempt.dto.AttemptQuestionResponse;
import com.examteam.exam_system.attempt.dto.AttemptResponse;
import com.examteam.exam_system.attempt.model.AttemptStatus;
import com.examteam.exam_system.attempt.model.ExamAttempt;
import com.examteam.exam_system.attempt.model.StudentAnswer;
import com.examteam.exam_system.attempt.repository.ExamAttemptRepository;
import com.examteam.exam_system.attempt.repository.StudentAnswerRepository;
import com.examteam.exam_system.attempt.validation.AnswerValidator;

import com.examteam.exam_system.exam.model.Exam;
import com.examteam.exam_system.exam.model.ExamStatus;
import com.examteam.exam_system.exam.model.ExamQuestion;
import com.examteam.exam_system.exam.repository.ExamRepository;
import com.examteam.exam_system.exam.repository.ExamQuestionRepository;

import com.examteam.exam_system.question.model.Question;
import com.examteam.exam_system.question.model.QuestionOption;
import com.examteam.exam_system.user.model.Student;
import com.examteam.exam_system.user.repository.StudentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamAttemptService {

    private final ExamAttemptRepository attemptRepository;
    private final StudentAnswerRepository answerRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentRepository studentRepository;

    public ExamAttemptService(
            ExamAttemptRepository attemptRepository,
            StudentAnswerRepository answerRepository,
            ExamRepository examRepository,
            ExamQuestionRepository examQuestionRepository,
            StudentRepository studentRepository
    ) {
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public AttemptResponse startAttempt(
            Long examId,
            String authenticatedEmail
    ) {
        Student student = findStudent(authenticatedEmail);

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Exam not found"
                ));

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Exam is not published"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(exam.getStartTime())
                || !now.isBefore(exam.getEndTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam is not available at this time"
            );
        }

        if (examQuestionRepository.countByExamId(examId) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Exam has no questions"
            );
        }

        if (attemptRepository.existsByExamIdAndStudentId(
                examId, student.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already started this exam"
            );
        }

        ExamAttempt attempt = new ExamAttempt(exam, student);

        return toResponse(attemptRepository.save(attempt));
    }

    @Transactional(readOnly = true)
    public List<AttemptQuestionResponse> getAttemptQuestions(
            Long attemptId,
            String authenticatedEmail
    ) {
        ExamAttempt attempt = findOwnedAttempt(
                attemptId, authenticatedEmail
        );

        requireActiveAttempt(attempt);

        return examQuestionRepository
                .findByExamId(attempt.getExam().getId())
                .stream()
                .map(ExamQuestion::getQuestion)
                .map(this::toQuestionResponse)
                .toList();
    }

    @Transactional
    public void saveAnswer(
            Long attemptId,
            AnswerRequest request,
            String authenticatedEmail
    ) {
        ExamAttempt attempt = findOwnedAttempt(
                attemptId, authenticatedEmail
        );

        requireActiveAttempt(attempt);

        if (request == null || request.questionId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Question ID is required"
            );
        }

        Long examId = attempt.getExam().getId();
        Long questionId = request.questionId();

        if (!examQuestionRepository.existsByExamIdAndQuestionId(
                examId, questionId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Question does not belong to this exam"
            );
        }

        Question question = examQuestionRepository
                .findByExamId(examId)
                .stream()
                .map(ExamQuestion::getQuestion)
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Question not found"
                ));

        QuestionOption selectedOption = null;

        if (request.selectedOptionId() != null) {

            List<Long> allowedIds = question.getOptions()
                    .stream()
                    .map(QuestionOption::getId)
                    .toList();

            if (!AnswerValidator.isValid(
                    request.selectedOptionId(), allowedIds)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selected option is invalid"
                );
            }

            selectedOption = question.getOptions()
                    .stream()
                    .filter(option -> option.getId()
                            .equals(request.selectedOptionId()))
                    .findFirst()
                    .orElseThrow();
        }

        StudentAnswer answer = answerRepository
                .findByAttemptIdAndQuestionId(attemptId, questionId)
                .orElseGet(() -> new StudentAnswer(
                        attempt, question, null
                ));

        answer.setSelectedOption(selectedOption);

        answerRepository.save(answer);
    }

    @Transactional
    public AttemptResponse submitAttempt(
            Long attemptId,
            String authenticatedEmail
    ) {
        ExamAttempt attempt = findOwnedAttempt(
                attemptId, authenticatedEmail
        );

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Attempt has already been submitted"
            );
        }

        // Allow submission after the deadline,
        // but no further answer changes.
        attempt.submit();

        return toResponse(attemptRepository.save(attempt));
    }

    private Student findStudent(String authenticatedEmail) {

        return studentRepository.findAll()
                .stream()
                .filter(student ->
                        student.isActive()
                                && student.getEmail()
                                .equalsIgnoreCase(authenticatedEmail))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Active student profile not found"
                ));
    }

    private ExamAttempt findOwnedAttempt(
            Long attemptId,
            String authenticatedEmail
    ) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Attempt not found"
                ));

        if (!attempt.getStudent().getEmail()
                .equalsIgnoreCase(authenticatedEmail)
                || !attempt.getStudent().isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access this attempt"
            );
        }

        return attempt;
    }

    private void requireActiveAttempt(ExamAttempt attempt) {

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Attempt is no longer active"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime examEnd = attempt.getExam().getEndTime();

        LocalDateTime personalDeadline =
                attempt.getStartedAt().plusMinutes(
                        attempt.getExam().getDurationMinutes()
                );

        if (!now.isBefore(examEnd)
                || !now.isBefore(personalDeadline)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam time has expired"
            );
        }
    }

    private AttemptQuestionResponse toQuestionResponse(
            Question question
    ) {
        List<AttemptQuestionResponse.OptionResponse> options =
                question.getOptions()
                        .stream()
                        .map(option ->
                                new AttemptQuestionResponse.OptionResponse(
                                        option.getId(),
                                        option.getText()
                                )
                        )
                        .toList();

        return new AttemptQuestionResponse(
                question.getId(),
                question.getText(),
                question.getMarks(),
                options
        );
    }

    private AttemptResponse toResponse(ExamAttempt attempt) {

        return new AttemptResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getStudent().getId(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                attempt.getStatus()
        );
    }
}
