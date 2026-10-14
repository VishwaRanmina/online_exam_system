package com.examteam.exam_system.user.service;

import com.examteam.exam_system.user.dto.AdminDashboardResponse;
import com.examteam.exam_system.user.repository.StudentRepository;
import com.examteam.exam_system.user.repository.LecturerRepository;
import com.examteam.exam_system.user.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;

    public AdminService(
            StudentRepository studentRepository,
            LecturerRepository lecturerRepository,
            UserRepository userRepository) {

        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardResponse getDashboardStatistics() {

        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByActiveTrue();

        long totalLecturers = lecturerRepository.count();
        long activeLecturers = lecturerRepository.countByActiveTrue();

        long totalUserAccounts = userRepository.count();
        long activeUserAccounts = userRepository.countByActiveTrue();

        return new AdminDashboardResponse(
                totalStudents,
                activeStudents,
                totalLecturers,
                activeLecturers,
                totalUserAccounts,
                activeUserAccounts
        );
    }
}