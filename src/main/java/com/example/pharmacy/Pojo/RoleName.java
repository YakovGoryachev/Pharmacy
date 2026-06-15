package com.example.pharmacy.Pojo;

public final class RoleName {
    public static final String PHARMACIST = "ПЕРВОСТОЛЬНИК";
    public static final String MANAGER = "ЗАВЕДУЮЩИЙ";
    /** Системный администратор: только пользователи и аудит */
    public static final String ADMIN = "АДМИНИСТРАТОР";
    public static final String ACCOUNTANT = "БУХГАЛТЕР";
    /** Доступ ко всем операционным модулям сети */
    public static final String DIRECTOR = "ДИРЕКТОР";
    public static final String NETWORK_OWNER = "ВЛАДЕЛЕЦ_СЕТИ";
    /** Учебная роль: доступ ко всем разделам */
    public static final String TEST = "ТЕСТ";

    private RoleName() {
    }

    public static boolean isSystemAdmin(String role) {
        return ADMIN.equals(role);
    }

    /** Сводная аналитика и обзор всей аптечной сети */
    public static boolean hasNetworkScope(String role) {
        return NETWORK_OWNER.equals(role) || DIRECTOR.equals(role) || TEST.equals(role);
    }

    public static boolean canManageSystemCategories(String role) {
        return hasNetworkScope(role);
    }
}
