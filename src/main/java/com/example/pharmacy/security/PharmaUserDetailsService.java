package com.example.pharmacy.security;

import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PharmaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public PharmaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginWithDetails(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new PharmaUserDetails(user);
    }
}
