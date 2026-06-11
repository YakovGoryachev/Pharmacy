package com.example.pharmacy.Configuration;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test")
public class TestDataInitializer {

    @Bean
    CommandLineRunner testSeed(RolesRepository rolesRepository,
                               UserRepository userRepository,
                               PharmacyRepository pharmacyRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            Roles pharmacist = new Roles();
            pharmacist.setName(RoleName.PHARMACIST);
            pharmacist.setDescription("Pharmacist");
            pharmacist = rolesRepository.save(pharmacist);

            Roles admin = new Roles();
            admin.setName(RoleName.ADMIN);
            admin.setDescription("Admin");
            admin = rolesRepository.save(admin);

            Roles manager = new Roles();
            manager.setName(RoleName.MANAGER);
            manager.setDescription("Manager");
            manager = rolesRepository.save(manager);

            Roles accountant = new Roles();
            accountant.setName(RoleName.ACCOUNTANT);
            accountant.setDescription("Accountant");
            accountant = rolesRepository.save(accountant);

            Roles test = new Roles();
            test.setName(RoleName.TEST);
            test.setDescription("Test all");
            test = rolesRepository.save(test);

            Pharmacy p = new Pharmacy();
            p.setName("Test Pharmacy");
            p.setActive(true);
            p = pharmacyRepository.save(p);

            User pharm = new User();
            pharm.setLogin("pharm1");
            pharm.setPassword(passwordEncoder.encode("pharm123"));
            pharm.setName("Test Pharm");
            pharm.setRole(pharmacist);
            pharm.setPharmacy(p);
            userRepository.save(pharm);

            User mgr = new User();
            mgr.setLogin("manager1");
            mgr.setPassword(passwordEncoder.encode("mgr123"));
            mgr.setName("Test Manager");
            mgr.setRole(manager);
            mgr.setPharmacy(p);
            userRepository.save(mgr);

            User acc = new User();
            acc.setLogin("accountant");
            acc.setPassword(passwordEncoder.encode("acc123"));
            acc.setName("Test Accountant");
            acc.setRole(accountant);
            acc.setPharmacy(p);
            userRepository.save(acc);

            User adm = new User();
            adm.setLogin("admin");
            adm.setPassword(passwordEncoder.encode("admin123"));
            adm.setName("Admin");
            adm.setRole(admin);
            userRepository.save(adm);

            User testUser = new User();
            testUser.setLogin("test");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setName("Test All");
            testUser.setRole(test);
            testUser.setPharmacy(p);
            userRepository.save(testUser);
        };
    }
}
