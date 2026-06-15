package com.example.pharmacy.security;

import com.example.pharmacy.Pojo.RoleName;
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
                        .requestMatchers("/login", "/css/**", "/static/**", "/api/atx/**", "/api/nomenclature/**").permitAll()
                        .requestMatchers("/admin/users", "/admin/users/**")
                        .hasAnyRole(RoleName.ADMIN, RoleName.TEST)
                        .requestMatchers("/admin/audit", "/admin/audit/**")
                        .hasAnyRole(RoleName.ADMIN, RoleName.TEST)
                        .requestMatchers("/admin/pharmacies", "/admin/pharmacies/**")
                        .hasAnyRole(RoleName.DIRECTOR, RoleName.NETWORK_OWNER, RoleName.TEST)
                        .requestMatchers("/api/categories/**")
                        .hasAnyRole(RoleName.MANAGER, RoleName.DIRECTOR, RoleName.NETWORK_OWNER, RoleName.TEST)
                        .requestMatchers("/dashboard")
                        .hasAnyRole(RoleName.PHARMACIST, RoleName.MANAGER, RoleName.DIRECTOR,
                                RoleName.NETWORK_OWNER, RoleName.ACCOUNTANT, RoleName.TEST)
                        .requestMatchers("/cashier/**").hasAnyRole(RoleName.PHARMACIST, RoleName.TEST)
                        .requestMatchers("/batches", "/batches/**",
                                "/nomenclature", "/nomenclature/**")
                        .hasAnyRole(RoleName.MANAGER, RoleName.DIRECTOR, RoleName.NETWORK_OWNER, RoleName.TEST)
                        .requestMatchers("/reports/**")
                        .hasAnyRole(RoleName.MANAGER, RoleName.DIRECTOR, RoleName.NETWORK_OWNER,
                                RoleName.ACCOUNTANT, RoleName.TEST)
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
