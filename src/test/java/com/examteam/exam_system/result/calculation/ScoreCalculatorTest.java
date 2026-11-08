package com.examteam.exam_system.result.calculation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    @Test
    void allCorrect_returnsTotalMarks() {
        int score = ScoreCalculator.calculateScore(
                List.of(11L, 22L, 33L),
                List.of(11L, 22L, 33L),
                List.of(5, 10, 5));
        assertEquals(20, score);
    }

    @Test
    void allIncorrect_returnsZero() {
        int score = ScoreCalculator.calculateScore(
                List.of(1L, 2L, 3L),
                List.of(11L, 22L, 33L),
                List.of(5, 10, 5));
        assertEquals(0, score);
    }

    @Test
    void mixedAnswers_returnsPartialScore() {
        int score = ScoreCalculator.calculateScore(
                List.of(11L, 22L, 33L),
                List.of(11L, 25L, 33L),
                List.of(5, 10, 5));
        assertEquals(10, score);
    }

    @Test
    void unansweredQuestion_addsZero() {
        List<Long> selected = new ArrayList<>(Arrays.asList(11L, null, 33L));
        int score = ScoreCalculator.calculateScore(
                selected,
                List.of(11L, 22L, 33L),
                List.of(5, 10, 5));
        assertEquals(10, score);
    }

    @Test
    void emptyExam_returnsZero() {
        int score = ScoreCalculator.calculateScore(
                List.of(), List.of(), List.of());
        assertEquals(0, score);
    }

    @Test
    void nullSelectedList_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        null, List.of(1L), List.of(5)));
    }

    @Test
    void nullCorrectList_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L), null, List.of(5)));
    }

    @Test
    void nullMarksList_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L), List.of(1L), null));
    }

    @Test
    void mismatchedSizes_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L, 2L), List.of(1L), List.of(5)));
    }

    @Test
    void nullCorrectAnswer_throws() {
        List<Long> correct = new ArrayList<>(Arrays.asList(1L, null));
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L, 2L), correct, List.of(5, 5)));
    }

    @Test
    void zeroMarks_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L), List.of(1L), List.of(0)));
    }

    @Test
    void negativeMarks_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L), List.of(1L), List.of(-5)));
    }

    @Test
    void nullMarksElement_throws() {
        List<Integer> marks = new ArrayList<>(Arrays.asList(5, null));
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L, 2L), List.of(1L, 2L), marks));
    }

    @Test
    void integerOverflow_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.calculateScore(
                        List.of(1L, 2L),
                        List.of(1L, 2L),
                        List.of(Integer.MAX_VALUE, 1)));
    }

    @Test
    void largeLongIds_comparedByValue() {
        int score = ScoreCalculator.calculateScore(
                List.of(1000L), List.of(1000L), List.of(5));
        assertEquals(5, score);
    }

    @Test
    void inputListsAreNotModified() {
        List<Long> selected = new ArrayList<>(List.of(11L, 22L));
        List<Long> correct = new ArrayList<>(List.of(11L, 25L));
        List<Integer> marks = new ArrayList<>(List.of(5, 10));

        ScoreCalculator.calculateScore(selected, correct, marks);

        assertEquals(List.of(11L, 22L), selected);
        assertEquals(List.of(11L, 25L), correct);
        assertEquals(List.of(5, 10), marks);
    }
}