
package com.examteam.exam_system.exam.service;

import com.examteam.exam_system.exam.dto.ExamRequest;
import com.examteam.exam_system.exam.dto.ExamResponse;
import com.examteam.exam_system.exam.model.Exam;
import com.examteam.exam_system.exam.model.ExamQuestion;
import com.examteam.exam_system.exam.model.ExamStatus;
import com.examteam.exam_system.exam.repository.ExamQuestionRepository;
import com.examteam.exam_system.exam.repository.ExamRepository;
import com.examteam.exam_system.exam.validation.ExamScheduleValidator;
import com.examteam.exam_system.question.model.Question;
import com.examteam.exam_system.question.repository.QuestionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;

    public ExamService(
            ExamRepository examRepository,
            ExamQuestionRepository examQuestionRepository,
            QuestionRepository questionRepository
    ) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request) {

        validateRequest(request);

        Exam exam = new Exam(
                request.title().trim(),
                request.startTime(),
                request.endTime(),
                request.durationMinutes()
        );

        return toResponse(examRepository.save(exam));
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getAllExams() {

        return examRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getPublishedExams() {

        return examRepository.findAll()
                .stream()
                .filter(exam ->
                        exam.getStatus() == ExamStatus.PUBLISHED)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {

        return toResponse(findExam(id));
    }

    @Transactional(readOnly = true)
    public ExamResponse getPublishedExamById(Long id) {

        Exam exam = findExam(id);

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Exam not found"
            );
        }

        return toResponse(exam);
    }

    @Transactional
    public ExamResponse updateExam(
            Long id,
            ExamRequest request
    ) {

        validateRequest(request);

        Exam exam = findExam(id);

        requireDraft(exam);

        exam.setTitle(request.title().trim());
        exam.setStartTime(request.startTime());
        exam.setEndTime(request.endTime());
        exam.setDurationMinutes(request.durationMinutes());

        return toResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse publishExam(Long id) {

        Exam exam = findExam(id);

        requireDraft(exam);

        if (examQuestionRepository.countByExamId(id) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot publish an exam without questions"
            );
        }

        exam.setStatus(ExamStatus.PUBLISHED);

        return toResponse(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse cancelExam(Long id) {

        Exam exam = findExam(id);

        if (exam.getStatus() == ExamStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exam is already cancelled"
            );
        }

        exam.setStatus(ExamStatus.CANCELLED);

        return toResponse(examRepository.save(exam));
    }

    @Transactional
    public void assignQuestion(
            Long examId,
            Long questionId
    ) {

        Exam exam = findExam(examId);

        requireDraft(exam);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Question not found: " + questionId
                        )
                );

        if (examQuestionRepository
                .existsByExamIdAndQuestionId(examId, questionId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Question already assigned to this exam"
            );
        }

        examQuestionRepository.save(
                new ExamQuestion(exam, question)
        );
    }

    @Transactional(readOnly = true)
    public List<Long> getQuestionIds(Long examId) {

        findExam(examId);

        return examQuestionRepository.findByExamId(examId)
                .stream()
                .map(mapping -> mapping.getQuestion().getId())
                .toList();
    }

    @Transactional
    public void removeQuestion(
            Long examId,
            Long questionId
    ) {

        Exam exam = findExam(examId);

        requireDraft(exam);

        ExamQuestion mapping =
                examQuestionRepository.findByExamId(examId)
                        .stream()
                        .filter(item ->
                                item.getQuestion().getId()
                                        .equals(questionId))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Question is not assigned to this exam"
                                )
                        );

        examQuestionRepository.delete(mapping);
    }

    private Exam findExam(Long id) {

        return examRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Exam not found: " + id
                        )
                );
    }

    private void requireDraft(Exam exam) {

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only draft exams can be modified"
            );
        }
    }

    private void validateRequest(ExamRequest request) {

        if (request == null ||
                request.title() == null ||
                request.title().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exam title is required"
            );
        }

        if (!ExamScheduleValidator.isValid(
                request.startTime(),
                request.endTime(),
                request.durationMinutes())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid examination schedule"
            );
        }
    }

    private ExamResponse toResponse(Exam exam) {

        return new ExamResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getStartTime(),
                exam.getEndTime(),
                exam.getDurationMinutes(),
                exam.getStatus()
        );
    }
}
