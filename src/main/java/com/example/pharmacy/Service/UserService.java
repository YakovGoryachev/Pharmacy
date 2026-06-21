package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.RoleName;
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
        User toSave;
        if (user.getId() != null) {
            toSave = userRepository.findById(user.getId())
                    .orElseThrow(() -> new BusinessException("Пользователь не найден"));
            String login = user.getLogin() != null ? user.getLogin().trim() : "";
            if (login.isBlank()) {
                throw new BusinessException("Укажите логин");
            }
            if (!login.equals(toSave.getLogin()) && userRepository.existsByLogin(login)) {
                throw new BusinessException("Логин уже занят");
            }
            toSave.setLogin(login);
            toSave.setName(user.getName());
            if (rawPassword != null && !rawPassword.isBlank()) {
                toSave.setPassword(passwordEncoder.encode(rawPassword));
            }
        } else {
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new BusinessException("Укажите логин");
            }
            if (userRepository.existsByLogin(user.getLogin().trim())) {
                throw new BusinessException("Логин уже занят");
            }
            if (rawPassword == null || rawPassword.isBlank()) {
                throw new BusinessException("Укажите пароль");
            }
            toSave = user;
            toSave.setLogin(user.getLogin().trim());
            toSave.setPassword(passwordEncoder.encode(rawPassword));
        }
        Roles role = rolesRepository.findById(roleId).orElseThrow();
        if (!RoleName.assignableByAdmin().contains(role.getName())) {
            throw new BusinessException("Администратор не может назначить эту роль");
        }
        if (RoleName.requiresPharmacy(role.getName()) && pharmacyId == null) {
            throw new BusinessException("Для роли «" + role.getDescription() + "» нужно указать аптеку");
        }
        toSave.setRole(role);
        if (pharmacyId != null) {
            Pharmacy p = pharmacyRepository.findById(pharmacyId).orElseThrow();
            toSave.setPharmacy(p);
        } else {
            toSave.setPharmacy(null);
        }
        return userRepository.save(toSave);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
