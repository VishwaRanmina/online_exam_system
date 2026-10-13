package com.examteam.exam_system.attempt.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnswerValidatorTest {

    private final List<Long> options = List.of(201L, 202L, 203L, 204L);

    @Test
    void allowedOption_returnsTrue() {
        assertTrue(AnswerValidator.isValid(203L, options));
    }

    @Test
    void unknownOption_returnsFalse() {
        assertFalse(AnswerValidator.isValid(999L, options));
    }

    @Test
    void nullSelectedId_returnsFalse() {
        assertFalse(AnswerValidator.isValid(null, options));
    }

    @Test
    void nullOptionsList_returnsFalse() {
        assertFalse(AnswerValidator.isValid(203L, null));
    }

    @Test
    void emptyOptionsList_returnsFalse() {
        assertFalse(AnswerValidator.isValid(203L, List.of()));
    }

    @Test
    void duplicateAllowedIds_returnsFalse() {
        List<Long> duplicates = List.of(201L, 202L, 202L, 204L);
        assertFalse(AnswerValidator.isValid(201L, duplicates));
    }

    @Test
    void nullIdInsideList_returnsFalse() {
        List<Long> withNull = Arrays.asList(201L, null, 203L);
        assertFalse(AnswerValidator.isValid(201L, withNull));
    }

    @Test
    void largeIds_comparedByValue_returnsTrue() {
        List<Long> bigIds = List.of(1000L, 2000L, 3000L);
        assertTrue(AnswerValidator.isValid(1000L, bigIds));
    }
}