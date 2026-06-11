package com.example.pharmacy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler,
                          LoginFailureHandler loginFailureHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/categories/**").hasAnyRole("MANAGER", "ADMIN", "TEST")
                        .requestMatchers("/login", "/css/**", "/static/**", "/api/atx/**", "/api/nomenclature/**").permitAll()
                        .requestMatchers("/dashboard").hasAnyRole("PHARMACIST", "MANAGER", "ADMIN", "ACCOUNTANT", "TEST")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "TEST")
                        .requestMatchers("/cashier/**").hasAnyRole("PHARMACIST", "TEST")
                        .requestMatchers("/batches", "/batches/**",
                                "/nomenclature", "/nomenclature/**").hasAnyRole("MANAGER", "ADMIN", "TEST")
                        .requestMatchers("/reports/**").hasAnyRole("MANAGER", "ADMIN", "ACCOUNTANT", "TEST")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}
