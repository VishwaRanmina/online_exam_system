package com.examteam.exam_system.user.dto;

public record LecturerResponse(
        Long id,
        String employeeNumber,
        String name,
        String email,
        boolean active
) {}