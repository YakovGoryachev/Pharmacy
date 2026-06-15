package com.example.pharmacy;

import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.security.PharmaUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/uchet-lp", "/pos"})
    public String legacyCashierRedirect() {
        return "redirect:/cashier";
    }

    @GetMapping({"/", "/hello"})
    public String home(@AuthenticationPrincipal PharmaUserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = principal.getUser();
        String role = user.getRole().getName();
        return switch (role) {
            case RoleName.ADMIN -> "redirect:/admin/users";
            case RoleName.PHARMACIST -> "redirect:/cashier";
            case RoleName.TEST, RoleName.MANAGER, RoleName.DIRECTOR,
                 RoleName.NETWORK_OWNER, RoleName.ACCOUNTANT -> "redirect:/dashboard";
            default -> "redirect:/login";
        };
    }
}
