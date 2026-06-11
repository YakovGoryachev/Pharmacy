package com.example.pharmacy.Controllers;

import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Pojo.RequestReport;
import com.example.pharmacy.Pojo.User;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Service.ReportService;
import com.example.pharmacy.exception.BusinessException;
import com.example.pharmacy.security.SecurityUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportService reportService;
    private final PharmacyRepository pharmacyRepository;

    public ReportsController(ReportService reportService, PharmacyRepository pharmacyRepository) {
        this.reportService = reportService;
        this.pharmacyRepository = pharmacyRepository;
    }

    @GetMapping({"", "/dashboard"})
    public String reportsHub(Model model) {
        User user = SecurityUtils.currentUser();
        Long pharmacyId = SecurityUtils.resolvePharmacyId(pharmacyRepository);
        if (user.getPharmacy() != null) {
            model.addAttribute("pharmacyName", user.getPharmacy().getName());
        } else if (pharmacyId != null) {
            pharmacyRepository.findById(pharmacyId).map(Pharmacy::getName)
                    .ifPresent(name -> model.addAttribute("pharmacyName", name + " (сводка)"));
        }

        Instant to = Instant.now();
        Instant from = to.minus(30, ChronoUnit.DAYS);
        model.addAttribute("metrics", reportService.dashboard(pharmacyId, from, to));
        model.addAttribute("pageTitle", "Отчёты");
        model.addAttribute("activeNav", "reports");
        return "reports-dashboard";
    }

    @GetMapping("/generate")
    public String generateForm(Model model) {
        model.addAttribute("pageTitle", "Формирование отчётов");
        model.addAttribute("activeNav", "reports");
        return "reports-generate";
    }

    @PostMapping("/stock")
    public String stockReport(RedirectAttributes ra) throws Exception {
        Long pharmacyId = requirePharmacyId();
        RequestReport rr = reportService.generateStockPdf(pharmacyId, SecurityUtils.currentUser());
        ra.addFlashAttribute("successMessage", "Отчёт сформирован: " + rr.getFilePath());
        return "redirect:/reports/history";
    }

    @PostMapping("/sales")
    public String salesReport(RedirectAttributes ra) throws Exception {
        Long pharmacyId = requirePharmacyId();
        Instant to = Instant.now();
        Instant from = to.minus(30, ChronoUnit.DAYS);
        RequestReport rr = reportService.generateSalesPdf(pharmacyId, SecurityUtils.currentUser(), from, to);
        ra.addFlashAttribute("successMessage", "Отчёт сформирован: " + rr.getFilePath());
        return "redirect:/reports/history";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("reports", reportService.history(SecurityUtils.currentUser().getId()));
        model.addAttribute("pageTitle", "История отчётов");
        model.addAttribute("activeNav", "reports");
        return "reports-history";
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> file(@RequestParam String path) {
        Path p = Path.of(path);
        Resource resource = new FileSystemResource(p);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private Long requirePharmacyId() {
        Long pharmacyId = SecurityUtils.resolvePharmacyId(pharmacyRepository);
        if (pharmacyId == null) {
            throw new BusinessException("Не назначена аптека — невозможно сформировать отчёт");
        }
        return pharmacyId;
    }
}
