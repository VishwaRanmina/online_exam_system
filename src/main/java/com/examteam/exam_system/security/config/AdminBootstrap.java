
package com.examteam.exam_system.security.config;

import com.examteam.exam_system.user.model.Role;
import com.examteam.exam_system.user.model.User;
import com.examteam.exam_system.user.repository.UserRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@ConditionalOnProperty(
        name = "app.bootstrap.admin.enabled",
        havingValue = "true"
)
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${APP_ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByRole(Role.ADMIN)) {
            System.out.println(
                    "Admin already exists. Bootstrap skipped."
            );
            return;
        }

        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "Admin bootstrap credentials are missing"
            );
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                    "Admin bootstrap password is too short"
            );
        }

        String email = adminEmail.trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                    "Bootstrap email is already registered"
            );
        }

        String hash = passwordEncoder.encode(adminPassword);

        User admin = new User(
                email,
                hash,
                Role.ADMIN
        );

        userRepository.save(admin);

        System.out.println(
                "Initial admin account created successfully."
        );
    }
}
