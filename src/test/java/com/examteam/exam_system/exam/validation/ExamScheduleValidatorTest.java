package com.examteam.exam_system.exam.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamScheduleValidatorTest {

    private final LocalDateTime nineAm = LocalDateTime.of(2026, 10, 12, 9, 0);
    private final LocalDateTime noon = LocalDateTime.of(2026, 10, 12, 12, 0);

    @Test
    void validSchedule() {
        assertTrue(ExamScheduleValidator.isValid(nineAm, noon, 60));
    }

    @Test
    void nullStart() {
        assertFalse(ExamScheduleValidator.isValid(null, noon, 60));
    }

    @Test
    void nullEnd() {
        assertFalse(ExamScheduleValidator.isValid(nineAm, null, 60));
    }

    @Test
    void endBeforeStart() {
        assertFalse(ExamScheduleValidator.isValid(noon, nineAm, 60));
    }

    @Test
    void equalStartAndEnd() {
        assertFalse(ExamScheduleValidator.isValid(nineAm, nineAm, 60));
    }

    @Test
    void zeroDuration() {
        assertFalse(ExamScheduleValidator.isValid(nineAm, noon, 0));
    }

    @Test
    void negativeDuration() {
        assertFalse(ExamScheduleValidator.isValid(nineAm, noon, -30));
    }

    @Test
    void durationGreaterThanWindow() {
        LocalDateTime tenAm = LocalDateTime.of(2026, 10, 12, 10, 0);
        assertFalse(ExamScheduleValidator.isValid(nineAm, tenAm, 120));
    }

    @Test
    void durationExactlyEqualToWindow() {
        assertTrue(ExamScheduleValidator.isValid(nineAm, noon, 180));
    }

    @Test
    void crossMidnightSchedule() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 12, 23, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 13, 1, 0);
        assertTrue(ExamScheduleValidator.isValid(start, end, 90));
    }
}