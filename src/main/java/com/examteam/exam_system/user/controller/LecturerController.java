package com.examteam.exam_system.user.controller;

import com.examteam.exam_system.user.dto.LecturerRequest;
import com.examteam.exam_system.user.dto.LecturerResponse;
import com.examteam.exam_system.user.service.LecturerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LecturerResponse createLecturer(
            @Valid @RequestBody LecturerRequest request) {

        return lecturerService.createLecturer(request);
    }

    @GetMapping
    public List<LecturerResponse> getAllLecturers() {
        return lecturerService.getAllLecturers();
    }

    @GetMapping("/{id}")
    public LecturerResponse getLecturerById(@PathVariable Long id) {
        return lecturerService.getLecturerById(id);
    }

    @PutMapping("/{id}")
    public LecturerResponse updateLecturer(
            @PathVariable Long id,
            @Valid @RequestBody LecturerRequest request) {

        return lecturerService.updateLecturer(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
    }
}