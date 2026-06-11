package com.example.pharmacy.service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import com.example.pharmacy.Service.ChequeService;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.DTO.CartItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChequeServiceTest {

    @Autowired ChequeService chequeService;
    @Autowired StockService stockService;
    @Autowired PharmacyRepository pharmacyRepository;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired UserRepository userRepository;
    @Autowired RolesRepository rolesRepository;
    @Autowired StockRepository stockRepository;

    private Pharmacy pharmacy;
    private User user;
    private Nomenclature nomenclature;
    private Batch batch;

    @BeforeEach
    void setUp() {
        pharmacy = pharmacyRepository.findAll().stream().findFirst().orElseGet(() -> {
            Pharmacy p = new Pharmacy();
            p.setName("Ch Test");
            p.setActive(true);
            return pharmacyRepository.save(p);
        });

        Roles role = rolesRepository.findAll().stream().findFirst().orElseGet(() -> {
            Roles r = new Roles();
            r.setName(RoleName.PHARMACIST);
            return rolesRepository.save(r);
        });

        user = userRepository.findByLogin("pharm1").orElseGet(() -> {
            User u = new User();
            u.setLogin("pharm1");
            u.setPassword("x");
            u.setName("Pharm");
            u.setRole(role);
            u.setPharmacy(pharmacy);
            return userRepository.save(u);
        });

        nomenclature = new Nomenclature();
        nomenclature.setBrandName("SaleDrug");
        nomenclature.setMnn("MNN");
        nomenclature.setMarked(false);
        nomenclature.setReceipt(false);
        nomenclature = nomenclatureRepository.save(nomenclature);

        batch = new Batch();
        batch.setNomenclature(nomenclature);
        batch.setBatchNumber("CH-1");
        batch.setExpiryDate(LocalDate.now().plusMonths(3));
        batch.setReceivedDate(LocalDate.now());
        batch.setQtyReceived(5);
        batch.setQtyInStock(0);
        batch.setPrice(15000);
        batch = batchRepository.save(batch);
        stockService.receiveStock(pharmacy.getId(), batch, 5);
        stockService.reserve(pharmacy.getId(), batch.getId(), 2);
    }

    @Test
    void checkoutReducesStockAndCreatesCheque() {
        CartItemDto item = new CartItemDto();
        item.setNomenclatureId(nomenclature.getId());
        item.setBatchId(batch.getId());
        item.setQuantity(2);
        item.setPrice(15000);
        item.setReceiptRequired(false);
        item.setMarked(false);

        Cheque cheque = chequeService.checkout(pharmacy.getId(), user, PaymentMethod.CASH, List.of(item));

        assertNotNull(cheque.getId());
        assertNotNull(cheque.getFiscalNumber());
        assertEquals(30000, cheque.getTotalAmount());

        Stock stock = stockRepository.findByPharmacyIdAndBatchId(pharmacy.getId(), batch.getId()).orElseThrow();
        assertEquals(3, stock.getQuantity());
        assertEquals(0, stock.getReserved());
    }
}
