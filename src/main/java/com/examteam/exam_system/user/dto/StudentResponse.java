package com.examteam.exam_system.user.dto;

public record StudentResponse(
        Long id,
        String studentNumber,
        String name,
        String email,
        boolean active
) {}