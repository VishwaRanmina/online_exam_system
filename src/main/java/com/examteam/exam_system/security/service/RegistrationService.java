
package com.examteam.exam_system.security.service;

import com.examteam.exam_system.user.dto.UserRequest;
import com.examteam.exam_system.user.dto.UserResponse;
import com.examteam.exam_system.user.model.Role;
import com.examteam.exam_system.user.model.User;
import com.examteam.exam_system.user.repository.UserRepository;
import com.examteam.exam_system.user.repository.StudentRepository;
import com.examteam.exam_system.user.repository.LecturerRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            LecturerRepository lecturerRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(UserRequest request) {

        // Step 1: Normalize email
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Step 2: Prevent duplicate accounts
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        // Step 3: Convert role text to enum
        Role role;

        try {
            role = Role.valueOf(
                    request.role().trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid user role"
            );
        }

        // Step 4: Verify existing profile
        if (role == Role.STUDENT) {

            if (!studentRepository
                    .existsByEmailAndActiveTrue(email)) {

                throw new IllegalArgumentException(
                        "Active student profile not found"
                );
            }

        } else if (role == Role.LECTURER) {

            if (!lecturerRepository
                    .existsByEmailAndActiveTrue(email)) {

                throw new IllegalArgumentException(
                        "Active lecturer profile not found"
                );
            }
        }

        // Step 5: Hash password
        String passwordHash = passwordEncoder.encode(
                request.password()
        );

        // Step 6: Create account
        User user = new User(
                email,
                passwordHash,
                role
        );

        // Step 7: Save to MySQL
        User savedUser = userRepository.save(user);

        // Step 8: Return safe response
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.isActive()
        );
    }
}
