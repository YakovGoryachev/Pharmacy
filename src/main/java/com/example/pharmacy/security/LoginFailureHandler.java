package com.example.pharmacy.security;

import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;

    public LoginFailureHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String login = request.getParameter("username");
        userRepository.findByLogin(login).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 3) {
                user.setLockedUntil(Instant.now().plusSeconds(15 * 60));
            }
            userRepository.save(user);
        });
        response.sendRedirect("/login?error");
    }
}
