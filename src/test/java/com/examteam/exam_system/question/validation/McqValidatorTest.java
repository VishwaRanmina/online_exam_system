package com.examteam.exam_system.question.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class McqValidatorTest {

    private final List<String> validOptions =
            List.of("Java", "Python", "HTML", "CSS");

    @Test
    void validQuestion_returnsTrue() {
        assertTrue(McqValidator.isValid(validOptions, 0, 5));
    }

    @Test
    void correctIndexBoundaries_0And3_areValid() {
        assertTrue(McqValidator.isValid(validOptions, 0, 1));
        assertTrue(McqValidator.isValid(validOptions, 3, 1));
    }

    @Test
    void nullOptions_returnsFalse() {
        assertFalse(McqValidator.isValid(null, 0, 5));
    }

    @Test
    void threeOptions_returnsFalse() {
        assertFalse(McqValidator.isValid(List.of("A", "B", "C"), 0, 5));
    }

    @Test
    void fiveOptions_returnsFalse() {
        assertFalse(McqValidator.isValid(List.of("A", "B", "C", "D", "E"), 0, 5));
    }

    @Test
    void duplicateOptions_returnsFalse() {
        assertFalse(McqValidator.isValid(List.of("Java", "Python", "Java", "CSS"), 0, 5));
    }

    @Test
    void duplicateOptions_ignoringCaseAndSpaces_returnsFalse() {
        assertFalse(McqValidator.isValid(List.of("Java", " java ", "HTML", "CSS"), 0, 5));
    }

    @Test
    void blankOption_returnsFalse() {
        assertFalse(McqValidator.isValid(List.of("Java", "    ", "HTML", "CSS"), 0, 5));
    }

    @Test
    void nullOption_returnsFalse() {
        // List.of does not allow null, so using Arrays.asList
        assertFalse(McqValidator.isValid(Arrays.asList("Java", null, "HTML", "CSS"), 0, 5));
    }

    @Test
    void negativeCorrectIndex_returnsFalse() {
        assertFalse(McqValidator.isValid(validOptions, -1, 5));
    }

    @Test
    void tooLargeCorrectIndex_returnsFalse() {
        assertFalse(McqValidator.isValid(validOptions, 4, 5));
    }

    @Test
    void zeroMarks_returnsFalse() {
        assertFalse(McqValidator.isValid(validOptions, 0, 0));
    }

    @Test
    void negativeMarks_returnsFalse() {
        assertFalse(McqValidator.isValid(validOptions, 0, -5));
    }
}