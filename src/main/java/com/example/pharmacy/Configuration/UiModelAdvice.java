package com.example.pharmacy.Configuration;

import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.security.PharmaUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class UiModelAdvice {

    @ModelAttribute
    public void currentUserProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof PharmaUserDetails details)) {
            return;
        }
        User user = details.getUser();
        model.addAttribute("currentUserLogin", user.getLogin());
        model.addAttribute("currentUserName", user.getName() != null ? user.getName() : user.getLogin());
        String roleLabel = user.getRole() != null
                ? (user.getRole().getDescription() != null ? user.getRole().getDescription() : user.getRole().getName())
                : "—";
        model.addAttribute("currentUserRole", roleLabel);
        if (user.getPharmacy() != null) {
            model.addAttribute("currentUserPharmacy", user.getPharmacy().getName());
            model.addAttribute("pharmacyName", user.getPharmacy().getName());
        } else {
            model.addAttribute("currentUserPharmacy", "Не назначена");
        }
        model.addAttribute("userInitials", initials(user.getName() != null ? user.getName() : user.getLogin()));
    }

    private static String initials(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
    }

    @ModelAttribute
    public void activeNav(HttpServletRequest request, Model model) {
        if (model.containsAttribute("activeNav")) {
            return;
        }
        String uri = request.getRequestURI();
        String nav = resolveActiveNav(uri);
        if (nav != null) {
            model.addAttribute("activeNav", nav);
        }
    }

    private static String resolveActiveNav(String uri) {
        if (uri.equals("/dashboard")) {
            return "dashboard";
        }
        if (uri.startsWith("/cashier")) {
            return "cashier";
        }
        if (uri.startsWith("/batches")) {
            return "batches";
        }
        if (uri.startsWith("/nomenclature")) {
            return "nomenclature";
        }
        if (uri.startsWith("/reports")) {
            return "reports";
        }
        if (uri.startsWith("/admin/pharmacies")) {
            return "pharmacies";
        }
        if (uri.startsWith("/admin/audit")) {
            return "audit";
        }
        if (uri.startsWith("/admin/users")) {
            return "admin";
        }
        if (uri.startsWith("/admin")) {
            return "admin";
        }
        return null;
    }
}
