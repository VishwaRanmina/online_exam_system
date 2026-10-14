package com.examteam.exam_system.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Public pages and frontend assets
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/style.css",
                                "/app.js",
                                "/error",
                                "/api/auth/csrf"
                        ).permitAll()

                        // Authentication endpoints
                        .requestMatchers(
                                "/api/auth/login"
                        ).permitAll()

                        // Only ADMIN can manage registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register"
                        ).hasRole("ADMIN")

                        // Admin endpoints
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Student and Lecturer management
                        .requestMatchers(
                                "/api/students/**",
                                "/api/lecturers/**"
                        ).hasRole("ADMIN")

                        // All remaining endpoints require login
                        .anyRequest().authenticated()
                )

                // Server-side session authentication
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                // Spring Security handles login
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("email")
                        .successHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value())
                        )
                        .failureHandler((request, response, exception) ->
                                response.sendError(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        "Invalid credentials"
                                )
                        )
                        .permitAll()
                )

                // Spring Security handles logout
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value())
                        )
                )

                // CSRF protection remains enabled
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                )



                // Return 401 instead of redirecting API users
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                );

        return http.build();
    }
}