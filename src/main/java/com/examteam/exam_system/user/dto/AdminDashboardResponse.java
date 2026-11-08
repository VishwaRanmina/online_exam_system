package com.examteam.exam_system.user.dto;

public record AdminDashboardResponse(
        long totalStudents,
        long activeStudents,
        long totalLecturers,
        long activeLecturers,
        long totalUserAccounts,
        long activeUserAccounts
) {}