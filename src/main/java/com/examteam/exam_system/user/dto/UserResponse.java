package com.examteam.exam_system.user.dto;

import com.examteam.exam_system.user.model.Role;

public record UserResponse(
        Long id,
        String email,
        Role role,
        boolean active
) {}