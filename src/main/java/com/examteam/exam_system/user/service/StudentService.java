package com.examteam.exam_system.user.service;

import com.examteam.exam_system.user.dto.StudentRequest;
import com.examteam.exam_system.user.dto.StudentResponse;
import com.examteam.exam_system.user.model.Student;
import com.examteam.exam_system.user.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(StudentRequest request) {

        if (studentRepository.existsByStudentNumber(
                request.studentNumber())) {
            throw new IllegalArgumentException(
                    "Student number already exists");
        }

        if (studentRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        Student student = new Student(
                request.studentNumber(),
                request.name(),
                request.email()
        );

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findByActiveTrue();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active student not found: " + id
                        ));
    }

    public Student updateStudent(Long id, Student updatedStudent) {

        Student existingStudent = getStudentById(id);

        if (studentRepository.existsByEmailAndIdNot(
                updatedStudent.getEmail(), id)) {

            throw new IllegalArgumentException(
                    "Email already belongs to another student"
            );
        }

        if (studentRepository.existsByStudentNumberAndIdNot(
                updatedStudent.getStudentNumber(), id)) {

            throw new IllegalArgumentException(
                    "Student number already belongs to another student"
            );
        }

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setStudentNumber(
                updatedStudent.getStudentNumber()
        );

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {

        Student student = getStudentById(id);
        student.setActive(false);

        studentRepository.save(student);
    }

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentNumber(),
                student.getName(),
                student.getEmail(),
                student.isActive()
        );
    }

}