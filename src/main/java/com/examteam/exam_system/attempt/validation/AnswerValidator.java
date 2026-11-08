package com.examteam.exam_system.attempt.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnswerValidator {

    public static boolean isValid(Long selectedOptionId, List<Long> allowedOptionIds) {

        // If selected option is null so the answer is invalid
        if (selectedOptionId == null) {
            return false;
        }

        // If the allowed list is null or empty so the answer is invalid
        if (allowedOptionIds == null || allowedOptionIds.isEmpty()) {
            return false;
        }

        // Check list for null IDs and duplicate IDs
        Set<Long> seen = new HashSet<>();
        for (Long id : allowedOptionIds) {
            if (id == null) {
                return false;          // the list contains a null ID
            }
            if (!seen.add(id)) {
                return false;          // add() returned false, so this ID is a duplicate
            }
        }

        // Is selected ID one of the allowed IDs?
        return seen.contains(selectedOptionId);
    }
}