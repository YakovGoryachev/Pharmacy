package com.example.pharmacy.Controllers;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.RolesRepository;
import com.example.pharmacy.Service.AuditService;
import com.example.pharmacy.Service.PharmacyService;
import com.example.pharmacy.Service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PharmacyService pharmacyService;
    private final RolesRepository rolesRepository;
    private final PharmacyRepository pharmacyRepository;
    private final AuditService auditService;

    @InitBinder("pharmacy")
    public void initPharmacyBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }

    public AdminController(UserService userService,
                           PharmacyService pharmacyService,
                           RolesRepository rolesRepository,
                           PharmacyRepository pharmacyRepository,
                           AuditService auditService) {
        this.userService = userService;
        this.pharmacyService = pharmacyService;
        this.rolesRepository = rolesRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pageTitle", "Пользователи");
        return "admin-users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", rolesRepository.findAll());
        model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
        model.addAttribute("pageTitle", "Новый пользователь");
        return "admin-user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam String password,
                           @RequestParam Long roleId,
                           @RequestParam(required = false) Long pharmacyId) {
        userService.save(user, password, roleId, pharmacyId);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/audit")
    public String audit(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("logs", auditService.findAll(PageRequest.of(page, 50)));
        model.addAttribute("pageTitle", "Журнал аудита");
        return "admin-audit";
    }

    @GetMapping("/pharmacies")
    public String pharmacies(Model model) {
        model.addAttribute("pharmacies", pharmacyService.findAll());
        model.addAttribute("pageTitle", "Аптеки");
        model.addAttribute("activeNav", "pharmacies");
        return "admin-pharmacies";
    }

    @GetMapping("/pharmacies/create")
    public String createPharmacyForm(Model model) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setAddress(new com.example.pharmacy.Pojo.Address());
        pharmacy.setActive(true);
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("pageTitle", "Новая аптека");
        model.addAttribute("activeNav", "pharmacies");
        return "admin-pharmacy-form";
    }

    @GetMapping("/pharmacies/edit/{id}")
    public String editPharmacyForm(@PathVariable Long id, Model model) {
        Pharmacy pharmacy = pharmacyService.findById(id);
        if (pharmacy.getAddress() == null) {
            pharmacy.setAddress(new com.example.pharmacy.Pojo.Address());
        }
        model.addAttribute("pharmacy", pharmacy);
        model.addAttribute("pageTitle", "Редактирование аптеки");
        model.addAttribute("activeNav", "pharmacies");
        return "admin-pharmacy-form";
    }

    @PostMapping("/pharmacies/save")
    public String savePharmacy(@ModelAttribute Pharmacy pharmacy, RedirectAttributes ra) {
        if (pharmacy.getId() != null && pharmacy.getId() <= 0) {
            pharmacy.setId(null);
        }
        if (pharmacy.getAddress() == null) {
            pharmacy.setAddress(new com.example.pharmacy.Pojo.Address());
        }
        pharmacyService.save(pharmacy);
        ra.addFlashAttribute("successMessage", "Аптека сохранена");
        return "redirect:/admin/pharmacies";
    }

    @PostMapping("/pharmacies/delete/{id}")
    public String deletePharmacy(@PathVariable Long id, RedirectAttributes ra) {
        pharmacyService.delete(id);
        ra.addFlashAttribute("successMessage", "Аптека удалена или деактивирована");
        return "redirect:/admin/pharmacies";
    }
}
