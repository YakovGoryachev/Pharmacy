package com.example.pharmacy.security;

import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof PharmaUserDetails details) {
            User user = details.getUser();
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            String role = user.getRole().getName();
            String target = switch (role) {
                case RoleName.ADMIN -> "/admin/users";
                case RoleName.PHARMACIST -> "/cashier";
                case RoleName.TEST, RoleName.MANAGER, RoleName.DIRECTOR,
                     RoleName.NETWORK_OWNER, RoleName.ACCOUNTANT -> "/dashboard";
                default -> "/dashboard";
            };
            response.sendRedirect(target);
            return;
        }
        response.sendRedirect("/");
    }
}
