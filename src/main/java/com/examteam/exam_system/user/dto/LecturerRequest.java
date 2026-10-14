package com.examteam.exam_system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LecturerRequest(

        @NotBlank(message = "Employee number is required")
        String employeeNumber,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email

) {}