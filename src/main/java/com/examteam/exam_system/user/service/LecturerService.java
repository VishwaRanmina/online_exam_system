package com.examteam.exam_system.user.service;

import com.examteam.exam_system.user.dto.LecturerRequest;
import com.examteam.exam_system.user.dto.LecturerResponse;
import com.examteam.exam_system.user.model.Lecturer;
import com.examteam.exam_system.user.repository.LecturerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    // Entity -> Response DTO
    private LecturerResponse toResponse(Lecturer lecturer) {
        return new LecturerResponse(
                lecturer.getId(),
                lecturer.getEmployeeNumber(),
                lecturer.getName(),
                lecturer.getEmail(),
                lecturer.isActive()
        );
    }

    // CREATE
    public LecturerResponse createLecturer(LecturerRequest request) {

        if (lecturerRepository.existsByEmployeeNumber(
                request.employeeNumber())) {
            throw new IllegalArgumentException(
                    "Employee number already exists");
        }

        if (lecturerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        Lecturer lecturer = new Lecturer(
                request.employeeNumber(),
                request.name(),
                request.email()
        );

        return toResponse(lecturerRepository.save(lecturer));
    }

    // READ ALL ACTIVE
    public List<LecturerResponse> getAllLecturers() {
        return lecturerRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ ONE ACTIVE
    public LecturerResponse getLecturerById(Long id) {
        Lecturer lecturer = findActiveLecturer(id);
        return toResponse(lecturer);
    }

    private Lecturer findActiveLecturer(Long id) {
        return lecturerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lecturer not found: " + id));
    }

    // UPDATE
    public LecturerResponse updateLecturer(
            Long id, LecturerRequest request) {

        Lecturer lecturer = findActiveLecturer(id);

        if (lecturerRepository.existsByEmployeeNumberAndIdNot(
                request.employeeNumber(), id)) {
            throw new IllegalArgumentException(
                    "Employee number belongs to another lecturer");
        }

        if (lecturerRepository.existsByEmailAndIdNot(
                request.email(), id)) {
            throw new IllegalArgumentException(
                    "Email belongs to another lecturer");
        }

        lecturer.setEmployeeNumber(request.employeeNumber());
        lecturer.setName(request.name());
        lecturer.setEmail(request.email());

        return toResponse(lecturerRepository.save(lecturer));
    }

    // SOFT DELETE
    public void deleteLecturer(Long id) {

        Lecturer lecturer = findActiveLecturer(id);

        lecturer.setActive(false);

        lecturerRepository.save(lecturer);
    }
}