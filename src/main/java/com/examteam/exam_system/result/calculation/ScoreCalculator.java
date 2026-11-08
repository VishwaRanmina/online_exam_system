package com.examteam.exam_system.result.calculation;

import java.util.List;

public class ScoreCalculator {

    public static int calculateScore(
            List<Long> selectedOptionIds,
            List<Long> correctOptionIds,
            List<Integer> marksPerQuestion) {

        //  Check that none of the lists are null
        if (selectedOptionIds == null
                || correctOptionIds == null
                || marksPerQuestion == null) {
            throw new IllegalArgumentException("Lists must not be null");
        }

        //  Check that all three lists have the same size
        if (selectedOptionIds.size() != correctOptionIds.size()
                || correctOptionIds.size() != marksPerQuestion.size()) {
            throw new IllegalArgumentException("List sizes must match");
        }

        int totalScore = 0;

        //  Empty lists: the loop does not run, so 0 is returned
        for (int i = 0; i < correctOptionIds.size(); i++) {
            Long selected = selectedOptionIds.get(i);
            Long correct = correctOptionIds.get(i);
            Integer marks = marksPerQuestion.get(i);

            //  Validate the correct ID and marks for every question
            if (correct == null) {
                throw new IllegalArgumentException(
                        "Correct option ID must not be null at index " + i);
            }
            if (marks == null || marks <= 0) {
                throw new IllegalArgumentException(
                        "Marks must be greater than zero at index " + i);
            }

            //  Unanswered question -> 0 marks
            if (selected == null) {
                continue;
            }

            //  Use equals() to compare not ==
            if (selected.equals(correct)) {
                try {
                    totalScore = Math.addExact(totalScore, marks);
                } catch (ArithmeticException e) {
                    throw new IllegalArgumentException(
                            "Score overflow detected", e);
                }
            }
        }

        return totalScore;
    }
}