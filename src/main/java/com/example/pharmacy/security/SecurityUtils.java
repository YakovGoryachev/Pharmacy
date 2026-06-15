package com.example.pharmacy.security;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.PharmacyRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof PharmaUserDetails details) {
            return details.getUser();
        }
        throw new IllegalStateException("Пользователь не авторизован");
    }

    public static boolean isSystemAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + RoleName.ADMIN).equals(a.getAuthority()));
    }

    public static boolean isSystemAdmin(User user) {
        if (user.getRole() != null && RoleName.isSystemAdmin(user.getRole().getName())) {
            return true;
        }
        return isSystemAdmin();
    }

    /** @deprecated используйте {@link #isSystemAdmin()} */
    @Deprecated
    public static boolean isAdmin() {
        return isSystemAdmin();
    }

    public static boolean hasNetworkScope() {
        User user = currentUser();
        return user.getRole() != null && RoleName.hasNetworkScope(user.getRole().getName());
    }

    public static boolean hasAssignedPharmacy() {
        return currentUser().getPharmacy() != null;
    }

    public static Long currentPharmacyId() {
        User user = currentUser();
        if (user.getPharmacy() == null) {
            throw new IllegalStateException("У пользователя не назначена аптека");
        }
        return user.getPharmacy().getId();
    }

    /** Аптека пользователя или первая активная (для отчётов и дашборда без привязки). */
    public static Long resolvePharmacyId(PharmacyRepository pharmacyRepository) {
        User user = currentUser();
        if (user.getPharmacy() != null) {
            return user.getPharmacy().getId();
        }
        return pharmacyRepository.findByActiveTrue().stream()
                .findFirst()
                .map(Pharmacy::getId)
                .orElse(null);
    }
}
