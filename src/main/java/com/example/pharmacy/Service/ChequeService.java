package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.ChequeRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.PrescriptionRepository;
import com.example.pharmacy.DTO.CartItemDto;
import com.example.pharmacy.DTO.PrescriptionFormDto;
import com.example.pharmacy.audit.Audited;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChequeService {

    private final ChequeRepository chequeRepository;
    private final NomenclatureRepository nomenclatureRepository;
    private final StockService stockService;
    private final MarkingCodeService markingCodeService;
    private final PharmacyRepository pharmacyRepository;
    private final BatchRepository batchRepository;
    private final PrescriptionRepository prescriptionRepository;

    public ChequeService(ChequeRepository chequeRepository,
                         NomenclatureRepository nomenclatureRepository,
                         StockService stockService,
                         MarkingCodeService markingCodeService,
                         PharmacyRepository pharmacyRepository,
                         BatchRepository batchRepository,
                         PrescriptionRepository prescriptionRepository) {
        this.chequeRepository = chequeRepository;
        this.nomenclatureRepository = nomenclatureRepository;
        this.stockService = stockService;
        this.markingCodeService = markingCodeService;
        this.pharmacyRepository = pharmacyRepository;
        this.batchRepository = batchRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    @Audited(entity = "Cheque", action = "SALE")
    public Cheque checkout(Long pharmacyId, User user, PaymentMethod paymentMethod, List<CartItemDto> cart) {
        if (cart == null || cart.isEmpty()) {
            throw new BusinessException("Чек пуст");
        }
        for (CartItemDto item : cart) {
            if (item.isReceiptRequired()) {
                PrescriptionFormDto rx = item.getPrescription();
                if (rx == null || !rx.isComplete()) {
                    throw new BusinessException("Для рецептурного препарата укажите данные рецепта: "
                            + item.getDisplayName());
                }
            }
            if (item.isMarked() && (item.getMarkingCode() == null || item.getMarkingCode().isBlank())) {
                throw new BusinessException("Отсканируйте код маркировки: " + item.getDisplayName());
            }
        }
        assertUniquePrescriptionsInCart(cart);
        assertPrescriptionsNotAlreadyDispensed(cart);

        Cheque cheque = new Cheque();
        cheque.setPharmacy(pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new BusinessException("Аптека не найдена")));
        cheque.setUser(user);
        cheque.setPaymentMethod(paymentMethod);
        cheque.setNumberCheque("CHK-" + System.currentTimeMillis());
        cheque.setFiscalNumber("FISC-EMU-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        cheque.setIsReturned(false);

        int total = 0;
        for (CartItemDto item : cart) {
            Nomenclature n = nomenclatureRepository.findById(item.getNomenclatureId()).orElseThrow();
            Long batchId = item.getBatchId() != null
                    ? item.getBatchId()
                    : stockService.pickFefoStock(pharmacyId, item.getNomenclatureId(), item.getQuantity()).getBatch().getId();

            ChequePosition pos = new ChequePosition();
            pos.setCheque(cheque);
            pos.setNomenclature(n);
            pos.setBatch(batchRepository.findById(batchId).orElseThrow());
            pos.setQuantity(item.getQuantity());
            pos.setCost(item.getPrice());
            pos.setSumOfPosition(item.getLineTotal());
            cheque.getChequePositions().add(pos);
            total += item.getLineTotal();

            stockService.releaseReserve(pharmacyId, batchId, item.getQuantity());
            stockService.commitSale(pharmacyId, batchId, item.getQuantity());

            if (item.isMarked()) {
                MarkingCode mc = markingCodeService.bindToSale(item.getMarkingCode(), pos);
                pos.setMarkingCode(mc);
            }
            if (item.isReceiptRequired() && item.getPrescription() != null) {
                pos.setPrescription(buildPrescription(item.getPrescription(), pos));
            }
        }
        cheque.setTotalAmount(total);
        return chequeRepository.save(cheque);
    }

    @Transactional
    public Cheque returnCheque(Long chequeId) {
        Cheque cheque = chequeRepository.findById(chequeId).orElseThrow();
        if (Boolean.TRUE.equals(cheque.getIsReturned())) {
            throw new BusinessException("Чек уже возвращён");
        }
        Long pharmacyId = cheque.getPharmacy().getId();
        for (ChequePosition pos : cheque.getChequePositions()) {
            stockService.restoreFromReturn(pharmacyId, pos.getBatch().getId(), pos.getQuantity());
            markingCodeService.returnToStock(pos);
        }
        cheque.setIsReturned(true);
        return chequeRepository.save(cheque);
    }

    private Prescription buildPrescription(PrescriptionFormDto form, ChequePosition pos) {
        Prescription rx = new Prescription();
        rx.setChequePosition(pos);
        rx.setPatientName(form.getPatientName());
        rx.setPatientBirthDate(form.getPatientBirthDate());
        rx.setDoctorName(form.getDoctorName());
        rx.setPrescriptionNumber(form.getPrescriptionNumber());
        rx.setPrescriptionSeries(form.getPrescriptionSeries());
        rx.setPrescriptionDate(form.getPrescriptionDate());
        rx.setLpuCode(form.getLpuCode());
        rx.setStatus("ACTIVE");
        return rx;
    }

    private static void assertUniquePrescriptionsInCart(List<CartItemDto> cart) {
        Map<String, String> seen = new HashMap<>();
        for (CartItemDto item : cart) {
            if (!item.isReceiptRequired() || item.getPrescription() == null) {
                continue;
            }
            String key = item.getPrescription().identityKey();
            if (key.equals("#")) {
                continue;
            }
            String previous = seen.put(key, item.getDisplayName());
            if (previous != null) {
                throw new BusinessException(
                        "Один рецепт нельзя использовать для разных препаратов: «"
                                + previous + "» и «" + item.getDisplayName() + "»");
            }
        }
    }

    private void assertPrescriptionsNotAlreadyDispensed(List<CartItemDto> cart) {
        for (CartItemDto item : cart) {
            if (!item.isReceiptRequired() || item.getPrescription() == null) {
                continue;
            }
            String number = item.getPrescription().getPrescriptionNumber();
            if (number == null || number.isBlank()) {
                continue;
            }
            if (prescriptionRepository.existsInActiveSale(number)) {
                throw new BusinessException(
                        "Рецепт «" + number.trim() + "» уже был использован при продаже. "
                                + "Укажите другой рецепт для «" + item.getDisplayName() + "»");
            }
        }
    }

    public List<Cheque> salesForPeriod(Long pharmacyId, Instant from, Instant to) {
        return chequeRepository.findSalesInPeriod(pharmacyId, from, to);
    }
}
