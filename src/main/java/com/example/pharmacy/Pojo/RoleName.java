package com.example.pharmacy.Pojo;

import java.util.List;

public final class RoleName {
    public static final String PHARMACIST = "ПЕРВОСТОЛЬНИК";
    public static final String MANAGER = "ЗАВЕДУЮЩИЙ";
    public static final String ADMIN = "АДМИНИСТРАТОР";
    public static final String ACCOUNTANT = "БУХГАЛТЕР";
    public static final String DIRECTOR = "ДИРЕКТОР";
    public static final String NETWORK_OWNER = "ВЛАДЕЛЕЦ_СЕТИ";
    public static final String TEST = "ТЕСТ";

    private RoleName() {
    }

    public static boolean isSystemAdmin(String role) {
        return ADMIN.equals(role);
    }

    public static boolean hasNetworkScope(String role) {
        return NETWORK_OWNER.equals(role) || DIRECTOR.equals(role) || TEST.equals(role);
    }

    public static boolean canManageSystemCategories(String role) {
        return hasNetworkScope(role);
    }

    public static List<String> assignableByAdmin() {
        return List.of(PHARMACIST, MANAGER, ACCOUNTANT);
    }

    public static boolean requiresPharmacy(String role) {
        return PHARMACIST.equals(role) || MANAGER.equals(role) || ACCOUNTANT.equals(role);
    }
}
