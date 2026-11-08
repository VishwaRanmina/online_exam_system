package com.examteam.exam_system.exam.validation;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExamScheduleValidator {

    public static boolean isValid(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int durationMinutes)
    {

        // Return false if start or end time is not included
        if (startTime == null || endTime == null) {
            return false;
        }

        // Return false if start time is not before end time or equal
        if (!startTime.isBefore(endTime)) {
            return false;
        }

        // Return false if duration is zero or negative
        if (durationMinutes <= 0) {
            return false;
        }

        // Return false if the exam duration exceed the total available time window
        long windowMinutes = Duration.between(startTime, endTime).toMinutes();
        return durationMinutes <= windowMinutes;
    }
}