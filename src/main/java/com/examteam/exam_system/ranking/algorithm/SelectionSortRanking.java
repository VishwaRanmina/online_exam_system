
package com.examteam.exam_system.ranking.algorithm;

public class SelectionSortRanking {

    private SelectionSortRanking() {
    }

    public static void sort(
            Long[] studentIds,
            String[] studentNumbers,
            int[] scores
    ) {
        if (studentIds == null ||
                studentNumbers == null ||
                scores == null ||
                studentIds.length != scores.length ||
                studentNumbers.length != scores.length) {

            throw new IllegalArgumentException(
                    "Invalid ranking data"
            );
        }

        int n = scores.length;

        for (int i = 0; i < n; i++) {
            if (studentIds[i] == null ||
                    studentNumbers[i] == null) {
                throw new IllegalArgumentException(
                        "Student ID and number cannot be null"
                );
            }
        }

        for (int i = 0; i < n - 1; i++) {

            int bestIndex = i;

            for (int j = i + 1; j < n; j++) {

                if (scores[j] > scores[bestIndex] ||
                        (scores[j] == scores[bestIndex] &&
                                studentNumbers[j].compareTo(
                                        studentNumbers[bestIndex]) < 0)) {

                    bestIndex = j;
                }
            }

            int tempScore = scores[i];
            scores[i] = scores[bestIndex];
            scores[bestIndex] = tempScore;

            Long tempId = studentIds[i];
            studentIds[i] = studentIds[bestIndex];
            studentIds[bestIndex] = tempId;

            String tempNumber = studentNumbers[i];
            studentNumbers[i] = studentNumbers[bestIndex];
            studentNumbers[bestIndex] = tempNumber;
        }
    }
}
