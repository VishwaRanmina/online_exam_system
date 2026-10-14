package com.examteam.exam_system.question.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class McqValidator {

    public static boolean isValid(
            List<String> options,
            int correctOptionIndex,
            int marks
    ) {
        if (options == null || options.size() != 4) {
            return false;
        }

        if (correctOptionIndex < 0 || correctOptionIndex >= 4) {
            return false;
        }

        if (marks <= 0) {
            return false;
        }

        Set<String> uniqueOptions = new HashSet<>();

        for (String option : options) {

            if (option == null || option.isBlank()) {
                return false;
            }

            String normalized = option.trim().toLowerCase();

            if (!uniqueOptions.add(normalized)) {
                return false;
            }
        }

        return true;
    }
}