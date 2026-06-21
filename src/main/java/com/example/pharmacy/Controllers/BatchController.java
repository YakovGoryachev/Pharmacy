package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Service.BatchService;
import com.example.pharmacy.Service.InventoryService;
import com.example.pharmacy.Service.MarkingCodeService;
import com.example.pharmacy.Service.NomenclatureService;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.Service.WriteOffService;
import com.example.pharmacy.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;
    private final NomenclatureService nomenclatureService;
    private final StockService stockService;
    private final WriteOffService writeOffService;
    private final InventoryService inventoryService;
    private final MarkingCodeService markingCodeService;
    private final com.example.pharmacy.Repository.PharmacyRepository pharmacyRepository;

    public BatchController(BatchService batchService,
                           NomenclatureService nomenclatureService,
                           StockService stockService,
                           WriteOffService writeOffService,
                           InventoryService inventoryService,
                           MarkingCodeService markingCodeService,
                           com.example.pharmacy.Repository.PharmacyRepository pharmacyRepository) {
        this.batchService = batchService;
        this.nomenclatureService = nomenclatureService;
        this.stockService = stockService;
        this.writeOffService = writeOffService;
        this.inventoryService = inventoryService;
        this.markingCodeService = markingCodeService;
        this.pharmacyRepository = pharmacyRepository;
    }

    @GetMapping("")
    public String main(@RequestParam(required = false) String numBatch,
                       @RequestParam(required = false) String supplier,
                       @RequestParam(required = false) LocalDate dateEntrance,
                       @RequestParam(required = false) LocalDate expiryDate,
                       @RequestParam(defaultValue = "receivedDate") String sort,
                       @RequestParam(defaultValue = "desc") String dir,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<Batch> bl = batchService.findFilteredBatches(numBatch, supplier, dateEntrance, expiryDate, page, size, sort, dir);
        model.addAttribute("batches", bl);
        model.addAttribute("currentPage", bl.getNumber());
        model.addAttribute("totalPages", bl.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);
        model.addAttribute("pageTitle", "Склад — партии");
        return "warehouse-main";
    }

    @GetMapping("/create")
    public String priemka(Model model) {
        model.addAttribute("batchCommand", new BatchDto());
        model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
        model.addAttribute("pageTitle", "Приёмка ЛП");
        model.addAttribute("activeNav", "batches");
        return "warehouse-acceptance";
    }

    @PostMapping("/create")
    @Transactional
    public String create(@ModelAttribute BatchDto bdto,
                         @RequestParam Long pharmacyId,
                         @RequestParam(required = false) List<String> markingCodes,
                         Model model,
                         RedirectAttributes ra) {
        if (bdto.getNomenclatureId() == null || bdto.getNomenclatureId() <= 0) {
            return acceptanceFormWithError(model, bdto, markingCodes, null,
                    "Выберите препарат из справочника", true);
        }
        NomenclatureDto nom = nomenclatureService.findById(bdto.getNomenclatureId());
        boolean marked = Boolean.TRUE.equals(nom.getMarked());
        int qty = bdto.getQtyReceived() != null ? bdto.getQtyReceived() : 0;
        try {
            markingCodeService.validateReceiptCodes(markingCodes, marked, qty);
        } catch (com.example.pharmacy.exception.BusinessException ex) {
            return acceptanceFormWithError(model, bdto, markingCodes, nom, ex.getMessage(), false);
        }
        Batch batch = batchService.saveWithStock(bdto, pharmacyId);
        markingCodeService.registerAllOnReceipt(batch, markingCodes, marked, qty);
        ra.addFlashAttribute("successMessage",
                "Приёмка успешно проведена. Партия «" + batch.getBatchNumber()
                        + "» оприходована на склад. Можно сразу оприходовать следующую партию.");
        return "redirect:/batches/create";
    }

    private String acceptanceFormWithError(Model model, BatchDto bdto, List<String> markingCodes,
                                           NomenclatureDto nom, String error, boolean nomenclatureError) {
        model.addAttribute("batchCommand", bdto);
        model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
        model.addAttribute("pageTitle", "Приёмка ЛП");
        model.addAttribute("activeNav", "batches");
        model.addAttribute("scannedMarkingCodes", markingCodes != null ? markingCodes : List.of());
        if (nomenclatureError) {
            model.addAttribute("nomenclatureError", error);
        } else {
            model.addAttribute("markingError", error);
        }
        if (nom != null) {
            model.addAttribute("selectedNomenclatureText", buildNomenclatureDisplay(nom));
            model.addAttribute("selectedNomenclatureMarked", nom.getMarked());
        } else if (bdto.getNomenclatureId() != null && bdto.getNomenclatureId() > 0) {
            NomenclatureDto selected = nomenclatureService.findById(bdto.getNomenclatureId());
            model.addAttribute("selectedNomenclatureText", buildNomenclatureDisplay(selected));
            model.addAttribute("selectedNomenclatureMarked", selected.getMarked());
        }
        return "warehouse-acceptance";
    }

    private static String buildNomenclatureDisplay(NomenclatureDto nom) {
        return nom.getBrandName()
                + (nom.getDosage() != null ? " " + nom.getDosage() + " " + nom.getDosageUnit() : "")
                + " (" + nom.getMnn() + ")";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        BatchDto bdto = batchService.findById(id);
        if (bdto == null) {
            return "redirect:/batches?error=notFound";
        }
        if (bdto.getNomenclatureId() != null) {
            NomenclatureDto nom = nomenclatureService.findById(bdto.getNomenclatureId());
            String displayText = nom.getBrandName()
                    + (nom.getDosage() != null ? " " + nom.getDosage() + " " + nom.getDosageUnit() : "")
                    + " (" + nom.getMnn() + ")";
            model.addAttribute("selectedNomenclatureText", displayText);
        }
        model.addAttribute("batchCommand", bdto);
        model.addAttribute("hasSelectedNomenclature", bdto.getNomenclatureId() != null);
        model.addAttribute("pageTitle", "Редактирование партии");
        model.addAttribute("activeNav", "batches");
        return "warehouse-edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute BatchDto bdto) {
        bdto.setId(id);
        batchService.update(bdto);
        return "redirect:/batches";
    }

    @PostMapping("/delete/{id}")
    public String deleteBatch(@PathVariable Long id, RedirectAttributes ra) {
        batchService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Запись о партии удалена");
        return "redirect:/batches";
    }

    @GetMapping("/expiring")
    public String expiring(@RequestParam(defaultValue = "30") int days, Model model) {
        boolean networkView = SecurityUtils.hasNetworkScope();
        Long pharmacyId = networkView ? null : SecurityUtils.currentPharmacyId();
        model.addAttribute("stocks", stockService.findExpiring(pharmacyId, days));
        model.addAttribute("networkView", networkView);
        model.addAttribute("days", days);
        model.addAttribute("pageTitle", networkView ? "Критические сроки (вся сеть)" : "Критические сроки");
        return "warehouse-expiring";
    }

    @GetMapping("/writeoff")
    public String writeOffForm(Model model) {
        if (!SecurityUtils.hasAssignedPharmacy()) {
            return "redirect:/batches";
        }
        model.addAttribute("pharmacyId", SecurityUtils.currentPharmacyId());
        model.addAttribute("reasons", WriteOffReason.values());
        model.addAttribute("pageTitle", "Списание ЛП");
        return "warehouse-writeoff";
    }

    @PostMapping("/writeoff")
    public String writeOff(@RequestParam String batchNumber,
                           @RequestParam int quantity,
                           @RequestParam WriteOffReason reason,
                           @RequestParam(required = false) String comment,
                           RedirectAttributes ra) {
        if (!SecurityUtils.hasAssignedPharmacy()) {
            return "redirect:/batches";
        }
        Long pharmacyId = SecurityUtils.currentPharmacyId();
        writeOffService.writeOff(pharmacyId, batchNumber, quantity, reason, comment, SecurityUtils.currentUser());
        ra.addFlashAttribute("successMessage", "Списание оформлено");
        return "redirect:/batches";
    }

    @GetMapping("/transfer")
    public String transferForm(Model model) {
        model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
        model.addAttribute("pageTitle", "Перемещение");
        return "warehouse-transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromPharmacyId,
                           @RequestParam Long toPharmacyId,
                           @RequestParam String batchNumber,
                           @RequestParam int quantity,
                           @RequestParam String waybillNumber,
                           RedirectAttributes ra) {
        stockService.transfer(fromPharmacyId, toPharmacyId, batchNumber, quantity, waybillNumber, SecurityUtils.currentUser());
        ra.addFlashAttribute("successMessage", "Перемещение выполнено");
        return "redirect:/batches";
    }

    @GetMapping("/inventory")
    public String inventoryList(Model model) {
        Long pharmacyId = SecurityUtils.currentPharmacyId();
        model.addAttribute("sessions", inventoryService.listByPharmacy(pharmacyId));
        model.addAttribute("pageTitle", "Инвентаризация");
        return "warehouse-inventory-list";
    }

    @PostMapping("/inventory/start")
    public String startInventory(RedirectAttributes ra) {
        User user = SecurityUtils.currentUser();
        Pharmacy pharmacy = user.getPharmacy();
        InventorySession session = inventoryService.startSession(pharmacy, user);
        return "redirect:/batches/inventory/" + session.getId();
    }

    @GetMapping("/inventory/{id}")
    public String inventorySession(@PathVariable Long id, Model model) {
        model.addAttribute("session", inventoryService.findById(id));
        model.addAttribute("pageTitle", "Инвентаризация");
        return "warehouse-inventory";
    }

    @PostMapping("/inventory/{id}/save")
    public String saveInventoryCounts(@PathVariable Long id,
                                      @RequestParam Map<String, String> params,
                                      RedirectAttributes ra) {
        Map<Long, Integer> actualByLineId = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("actual_")) {
                Long lineId = Long.parseLong(entry.getKey().substring("actual_".length()));
                actualByLineId.put(lineId, Integer.parseInt(entry.getValue()));
            }
        }
        inventoryService.updateActualQuantities(id, actualByLineId);
        ra.addFlashAttribute("successMessage", "Фактические остатки сохранены");
        return "redirect:/batches/inventory/" + id;
    }

    @PostMapping("/inventory/{id}/complete")
    public String completeInventory(@PathVariable Long id, RedirectAttributes ra) {
        inventoryService.completeSession(id);
        ra.addFlashAttribute("successMessage", "Инвентаризация завершена");
        return "redirect:/batches/inventory";
    }

    @GetMapping("/marking")
    public String markingJournal(Model model) {
        model.addAttribute("codes", markingCodeService.listJournal());
        model.addAttribute("pageTitle", "Журнал маркировки");
        return "warehouse-marking";
    }

    @PostMapping("/marking/send-mdlp")
    public String sendMdlp(RedirectAttributes ra) {
        int count = markingCodeService.sendPendingToMdlp();
        ra.addFlashAttribute("successMessage",
                count > 0 ? "В МДЛП отправлено кодов: " + count : "Нет кодов, ожидающих отправки в МДЛП");
        return "redirect:/batches/marking";
    }
}
