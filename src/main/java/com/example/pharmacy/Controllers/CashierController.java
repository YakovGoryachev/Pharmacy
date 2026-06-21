package com.example.pharmacy.Controllers;

import com.example.pharmacy.Pojo.Cheque;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Pojo.PaymentMethod;
import com.example.pharmacy.Pojo.Stock;
import com.example.pharmacy.Repository.ChequeRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Service.CashierCatalogService;
import com.example.pharmacy.Service.ChequeService;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.DTO.CartItemDto;
import com.example.pharmacy.DTO.CashierCatalogPageDto;
import com.example.pharmacy.DTO.PrescriptionFormDto;
import com.example.pharmacy.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/cashier")
@SessionAttributes("cart")
public class CashierController {

    private final CashierCatalogService cashierCatalogService;
    private final NomenclatureRepository nomenclatureRepository;
    private final PharmacyRepository pharmacyRepository;
    private final StockService stockService;
    private final ChequeService chequeService;
    private final ChequeRepository chequeRepository;

    public CashierController(CashierCatalogService cashierCatalogService,
                             NomenclatureRepository nomenclatureRepository,
                             PharmacyRepository pharmacyRepository,
                             StockService stockService,
                             ChequeService chequeService,
                             ChequeRepository chequeRepository) {
        this.cashierCatalogService = cashierCatalogService;
        this.nomenclatureRepository = nomenclatureRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.stockService = stockService;
        this.chequeService = chequeService;
        this.chequeRepository = chequeRepository;
    }

    @ModelAttribute("cart")
    public List<CartItemDto> cart() {
        return new ArrayList<>();
    }

    @GetMapping("")
    public String main(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long pharmacyId,
                       @RequestParam(required = false) Double priceMin,
                       @RequestParam(required = false) Double priceMax,
                       @RequestParam(defaultValue = "brandName") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @ModelAttribute("cart") List<CartItemDto> cart,
                       Model model) {
        var user = SecurityUtils.currentUser();
        Long userPharmacyId = user.getPharmacy() != null ? user.getPharmacy().getId() : null;
        if (user.getPharmacy() != null) {
            model.addAttribute("pharmacyName", user.getPharmacy().getName());
        }

        Integer priceMinKop = priceMin != null ? (int) Math.round(priceMin * 100) : null;
        Integer priceMaxKop = priceMax != null ? (int) Math.round(priceMax * 100) : null;

        CashierCatalogPageDto catalog = cashierCatalogService.listProducts(
                q, pharmacyId, priceMinKop, priceMaxKop, sort, dir, userPharmacyId, page, size);

        model.addAttribute("searchQuery", q);
        model.addAttribute("catalogRows", catalog.getContent());
        model.addAttribute("currentPage", catalog.getCurrentPage());
        model.addAttribute("totalPages", catalog.getTotalPages());
        model.addAttribute("totalElements", catalog.getTotalElements());
        model.addAttribute("pageSize", catalog.getPageSize());
        model.addAttribute("pharmacies", pharmacyRepository.findByActiveTrue());
        model.addAttribute("filterPharmacyId", pharmacyId);
        model.addAttribute("filterPriceMin", priceMin);
        model.addAttribute("filterPriceMax", priceMax);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);
        model.addAttribute("userPharmacyId", userPharmacyId);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cart.stream().mapToInt(CartItemDto::getLineTotal).sum());
        model.addAttribute("paymentMethods", PaymentMethod.forCashier());
        model.addAttribute("cartNeedsRx", cart.stream().anyMatch(CartItemDto::isReceiptRequired));
        model.addAttribute("pageTitle", "АРМ кассира");
        model.addAttribute("activeNav", "cashier");
        return "cashier-main";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long nomenclatureId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) String markingCode,
                            @ModelAttribute("cart") List<CartItemDto> cart,
                            RedirectAttributes ra) {
        Long pharmacyId = SecurityUtils.currentPharmacyId();
        Nomenclature n = nomenclatureRepository.findById(nomenclatureId).orElseThrow();
        Stock stock = stockService.pickFefoStock(pharmacyId, nomenclatureId, quantity);
        stockService.reserve(pharmacyId, stock.getBatch().getId(), quantity);

        CartItemDto item = new CartItemDto();
        item.setNomenclatureId(n.getId());
        item.setBatchId(stock.getBatch().getId());
        item.setStockId(stock.getId());
        item.setDisplayName(n.getBrandName() + " (" + n.getMnn() + ")");
        item.setQuantity(quantity);
        int batchPrice = stock.getBatch().getPrice() != null ? stock.getBatch().getPrice() : 0;
        item.setPrice(batchPrice);
        item.setReceiptRequired(n.requiresPrescription());
        item.setNarcotic(Boolean.TRUE.equals(n.getNarcotic()));
        item.setPsychotropic(Boolean.TRUE.equals(n.getPsychotropic()));
        item.setMarked(Boolean.TRUE.equals(n.getMarked()));
        item.setMarkingCode(markingCode);
        cart.add(item);
        return "redirect:/cashier";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam int index,
                                 @ModelAttribute("cart") List<CartItemDto> cart) {
        if (index >= 0 && index < cart.size()) {
            CartItemDto item = cart.remove(index);
            Long pharmacyId = SecurityUtils.currentPharmacyId();
            stockService.releaseReserve(pharmacyId, item.getBatchId(), item.getQuantity());
        }
        return "redirect:/cashier";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam PaymentMethod paymentMethod,
                           @ModelAttribute("cart") List<CartItemDto> cart,
                           HttpServletRequest request,
                           RedirectAttributes ra) {
        if (!paymentMethod.isAvailableAtCashier()) {
            ra.addFlashAttribute("errorMessage", "Выбранный способ оплаты недоступен на кассе");
            return "redirect:/cashier";
        }
        bindPrescriptions(cart, request);
        Long pharmacyId = SecurityUtils.currentPharmacyId();
        Cheque cheque = chequeService.checkout(pharmacyId, SecurityUtils.currentUser(), paymentMethod, new ArrayList<>(cart));
        cart.clear();
        ra.addFlashAttribute("successMessage", "Продажа оформлена, чек " + cheque.getNumberCheque());
        return "redirect:/cashier/receipt/" + cheque.getId();
    }

    private static void bindPrescriptions(List<CartItemDto> cart, HttpServletRequest request) {
        for (int i = 0; i < cart.size(); i++) {
            CartItemDto item = cart.get(i);
            if (!item.isReceiptRequired()) {
                continue;
            }
            PrescriptionFormDto rx = new PrescriptionFormDto();
            rx.setPatientName(trim(request.getParameter("rxPatientName_" + i)));
            rx.setPrescriptionNumber(trim(request.getParameter("rxNumber_" + i)));
            String date = request.getParameter("rxDate_" + i);
            if (date != null && !date.isBlank()) {
                rx.setPrescriptionDate(LocalDate.parse(date));
            }
            rx.setLpuCode(trim(request.getParameter("rxLpu_" + i)));
            item.setPrescription(rx);
        }
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @GetMapping("/receipt/{id}")
    public String receipt(@PathVariable Long id, Model model) {
        Cheque cheque = chequeRepository.findById(id).orElseThrow();
        var user = SecurityUtils.currentUser();
        if (user.getPharmacy() != null) {
            model.addAttribute("pharmacyName", user.getPharmacy().getName());
        }
        model.addAttribute("cheque", cheque);
        model.addAttribute("pageTitle", "Чек " + cheque.getNumberCheque());
        return "cashier-receipt";
    }

    @GetMapping("/returns")
    public String returns(Model model) {
        Long pharmacyId = SecurityUtils.currentPharmacyId();
        var user = SecurityUtils.currentUser();
        if (user.getPharmacy() != null) {
            model.addAttribute("pharmacyName", user.getPharmacy().getName());
        }
        model.addAttribute("cheques", chequeRepository.findByPharmacyIdOrderByCreatedAtDesc(pharmacyId));
        model.addAttribute("pageTitle", "Возвраты");
        return "cashier-returns";
    }

    @PostMapping("/returns/{id}")
    public String processReturn(@PathVariable Long id, RedirectAttributes ra) {
        chequeService.returnCheque(id);
        ra.addFlashAttribute("successMessage", "Возврат оформлен");
        return "redirect:/cashier/returns";
    }
}
