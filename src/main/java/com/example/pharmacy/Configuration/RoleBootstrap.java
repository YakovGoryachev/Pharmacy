package com.example.pharmacy.Configuration;

import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Pojo.Roles;
import com.example.pharmacy.Repository.RolesRepository;

import java.util.Map;

public final class RoleBootstrap {

    private RoleBootstrap() {
    }

    public static void migrateAndEnsure(RolesRepository rolesRepository) {
        Map<String, String> legacyNames = Map.of(
                "PHARMACIST", RoleName.PHARMACIST,
                "MANAGER", RoleName.MANAGER,
                "ADMIN", RoleName.ADMIN,
                "ACCOUNTANT", RoleName.ACCOUNTANT,
                "TEST", RoleName.TEST
        );
        legacyNames.forEach((legacy, current) ->
                rolesRepository.findByName(legacy).ifPresent(role -> {
                    role.setName(current);
                    rolesRepository.save(role);
                }));

        upsertRole(rolesRepository, RoleName.PHARMACIST, "Первостольник (кассир)");
        upsertRole(rolesRepository, RoleName.MANAGER, "Заведующий аптекой");
        upsertRole(rolesRepository, RoleName.ACCOUNTANT, "Бухгалтер");
        upsertRole(rolesRepository, RoleName.DIRECTOR, "Директор");
        upsertRole(rolesRepository, RoleName.NETWORK_OWNER, "Владелец аптечной сети");
        upsertRole(rolesRepository, RoleName.ADMIN, "Системный администратор");
        upsertRole(rolesRepository, RoleName.TEST, "Тест: все разделы");
    }

    private static Roles upsertRole(RolesRepository repo, String name, String description) {
        return repo.findByName(name).map(role -> {
            if (description.equals(role.getDescription())) {
                return role;
            }
            role.setDescription(description);
            return repo.save(role);
        }).orElseGet(() -> {
            Roles role = new Roles();
            role.setName(name);
            role.setDescription(description);
            return repo.save(role);
        });
    }
}
