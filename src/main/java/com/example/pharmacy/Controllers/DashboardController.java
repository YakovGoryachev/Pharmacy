package com.example.pharmacy.Controllers;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Service.ReportService;
import com.example.pharmacy.security.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Controller
public class DashboardController {

    private final ReportService reportService;
    private final PharmacyRepository pharmacyRepository;

    public DashboardController(ReportService reportService, PharmacyRepository pharmacyRepository) {
        this.reportService = reportService;
        this.pharmacyRepository = pharmacyRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) Long pharmacyId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                            Model model) {
        User user = SecurityUtils.currentUser();
        String role = user.getRole().getName();
        boolean isNetworkAnalytics = RoleName.hasNetworkScope(role);
        boolean isManagerAnalytics = RoleName.MANAGER.equals(role);

        ZoneId zone = ZoneId.systemDefault();
        Instant toInstant = to != null
                ? to.atTime(23, 59, 59).atZone(zone).toInstant()
                : Instant.now();
        Instant fromInstant = from != null
                ? from.atStartOfDay(zone).toInstant()
                : toInstant.minus(30, ChronoUnit.DAYS);

        Long effectivePharmacyId;
        if (isNetworkAnalytics) {
            effectivePharmacyId = pharmacyId;
            model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
            model.addAttribute("selectedPharmacyId", pharmacyId);
            model.addAttribute("networkView", pharmacyId == null);
            if (pharmacyId != null) {
                pharmacyRepository.findById(pharmacyId).map(Pharmacy::getName)
                        .ifPresent(name -> model.addAttribute("pharmacyName", name));
            } else {
                model.addAttribute("pharmacyName", "Вся аптечная сеть");
            }
        } else if (isManagerAnalytics) {
            effectivePharmacyId = user.getPharmacy() != null
                    ? user.getPharmacy().getId()
                    : SecurityUtils.resolvePharmacyId(pharmacyRepository);
            if (user.getPharmacy() != null) {
                model.addAttribute("pharmacyName", user.getPharmacy().getName());
            } else if (effectivePharmacyId != null) {
                pharmacyRepository.findById(effectivePharmacyId).map(Pharmacy::getName)
                        .ifPresent(name -> model.addAttribute("pharmacyName", name));
            }
        } else {
            effectivePharmacyId = SecurityUtils.resolvePharmacyId(pharmacyRepository);
            if (user.getPharmacy() != null) {
                model.addAttribute("pharmacyName", user.getPharmacy().getName());
            }
        }

        model.addAttribute("fromDate", fromInstant.atZone(zone).toLocalDate());
        model.addAttribute("toDate", toInstant.atZone(zone).toLocalDate());
        model.addAttribute("isNetworkAnalytics", isNetworkAnalytics);
        model.addAttribute("isManagerAnalytics", isManagerAnalytics);
        model.addAttribute("networkView", Boolean.FALSE);

        if (isNetworkAnalytics || isManagerAnalytics) {
            model.addAttribute("analytics", reportService.buildPharmacyAnalytics(
                    effectivePharmacyId, fromInstant, toInstant));
            model.addAttribute("showDetailedAnalytics", true);
        } else {
            model.addAttribute("metrics", reportService.dashboard(effectivePharmacyId, fromInstant, toInstant));
            model.addAttribute("charts", reportService.dashboardCharts(effectivePharmacyId, fromInstant, toInstant));
            model.addAttribute("showDetailedAnalytics", false);
        }

        model.addAttribute("pageTitle", "Дашборд");
        model.addAttribute("activeNav", "dashboard");
        return "dashboard";
    }
}
