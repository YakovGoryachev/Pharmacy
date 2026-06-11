package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.Roles;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.RolesRepository;
import com.example.pharmacy.Repository.UserRepository;
import com.example.pharmacy.audit.Audited;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RolesRepository rolesRepository,
                       PharmacyRepository pharmacyRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    @Audited(entity = "User", action = "SAVE")
    public User save(User user, String rawPassword, Long roleId, Long pharmacyId) {
        if (user.getId() == null && userRepository.existsByLogin(user.getLogin())) {
            throw new BusinessException("Логин уже занят");
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        Roles role = rolesRepository.findById(roleId).orElseThrow();
        user.setRole(role);
        if (pharmacyId != null) {
            Pharmacy p = pharmacyRepository.findById(pharmacyId).orElseThrow();
            user.setPharmacy(p);
        } else {
            user.setPharmacy(null);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
