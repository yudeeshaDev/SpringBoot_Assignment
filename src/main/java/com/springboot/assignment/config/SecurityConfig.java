package com.springboot.assignment.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Value("${api.internal.key}")
    private String internalAPIKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/employees/**"))
                        .access(customAuthorizationManager())
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> sendErrorResponse(response, 403, "Invalid API Key"))
                        .authenticationEntryPoint((request, response, authException) -> sendErrorResponse(response, 401, "Missing API Key"))
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic Auth
                .formLogin(formLogin -> formLogin.disable()); // Disable Form Login
        return http.build();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager() {
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();

            String requestToken = request.getHeader("Authorization");
            String expectedToken = "Bearer " + internalAPIKey; // Ensure authToken is initialized

            if (requestToken == null || requestToken.isEmpty()) {
                throw new AccessDeniedException("Missing API Key");
            }

            if (!requestToken.equals(expectedToken)) {
                throw new AccessDeniedException("Invalid API Key");
            }
            return new AuthorizationDecision(true);
        };
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
