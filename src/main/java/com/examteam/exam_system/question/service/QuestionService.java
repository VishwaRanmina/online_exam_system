
package com.examteam.exam_system.question.service;

import com.examteam.exam_system.question.dto.QuestionRequest;
import com.examteam.exam_system.question.dto.QuestionResponse;
import com.examteam.exam_system.question.dto.StaffQuestionResponse;
import com.examteam.exam_system.question.model.Question;
import com.examteam.exam_system.question.model.QuestionOption;
import com.examteam.exam_system.question.repository.QuestionRepository;
import com.examteam.exam_system.question.validation.McqValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {

        validateQuestion(request);

        Question question = new Question(
                request.text().trim(),
                request.marks()
        );

        addOptions(question, request);

        Question saved = questionRepository.save(question);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {

        return questionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {

        Question question = findQuestion(id);

        return toResponse(question);
    }

    @Transactional
    public QuestionResponse updateQuestion(
            Long id,
            QuestionRequest request
    ) {

        validateQuestion(request);

        Question question = findQuestion(id);

        question.setText(request.text().trim());
        question.setMarks(request.marks());

        question.clearOptions();
        addOptions(question, request);

        Question saved = questionRepository.save(question);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StaffQuestionResponse getStaffQuestionById(Long id) {

        Question question = findQuestion(id);

        Long correctOptionId = question.getOptions()
                .stream()
                .filter(QuestionOption::isCorrect)
                .map(QuestionOption::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Question has no correct option"
                        )
                );

        return new StaffQuestionResponse(
                toResponse(question),
                correctOptionId
        );
    }

    @Transactional
    public void deleteQuestion(Long id) {

        Question question = findQuestion(id);

        questionRepository.delete(question);
    }

    private Question findQuestion(Long id) {

        return questionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Question not found: " + id
                        )
                );
    }

    private void validateQuestion(QuestionRequest request) {

        if (!McqValidator.isValid(
                request.options(),
                request.correctOptionIndex(),
                request.marks()
        )) {
            throw new IllegalArgumentException(
                    "Invalid MCQ. Provide four unique options, " +
                            "a valid correct option index and positive marks."
            );
        }

        if (request.text() == null ||
                request.text().isBlank()) {

            throw new IllegalArgumentException(
                    "Question text cannot be blank."
            );
        }
    }

    private void addOptions(
            Question question,
            QuestionRequest request
    ) {

        for (int i = 0; i < request.options().size(); i++) {

            boolean isCorrect =
                    i == request.correctOptionIndex();

            QuestionOption option = new QuestionOption(
                    request.options().get(i).trim(),
                    isCorrect
            );

            question.addOption(option);
        }
    }

    private QuestionResponse toResponse(Question question) {

        List<QuestionResponse.OptionResponse> options =
                question.getOptions()
                        .stream()
                        .map(option ->
                                new QuestionResponse.OptionResponse(
                                        option.getId(),
                                        option.getText()
                                )
                        )
                        .toList();

        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getMarks(),
                options
        );
    }
}
