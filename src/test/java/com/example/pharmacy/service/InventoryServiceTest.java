package com.example.pharmacy.service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import com.example.pharmacy.Service.InventoryService;
import com.example.pharmacy.Service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventoryServiceTest {

    @Autowired InventoryService inventoryService;
    @Autowired StockService stockService;
    @Autowired PharmacyRepository pharmacyRepository;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired UserRepository userRepository;
    @Autowired RolesRepository rolesRepository;
    @Autowired StockRepository stockRepository;

    private Pharmacy pharmacy;
    private User user;
    private Batch batch;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy();
        pharmacy.setName("Inv Test");
        pharmacy.setActive(true);
        pharmacy = pharmacyRepository.save(pharmacy);

        Roles role = new Roles();
        role.setName(RoleName.MANAGER);
        role = rolesRepository.save(role);

        user = new User();
        user.setLogin("mgr-inv");
        user.setPassword("x");
        user.setName("Mgr");
        user.setRole(role);
        user.setPharmacy(pharmacy);
        user = userRepository.save(user);

        Nomenclature n = new Nomenclature();
        n.setBrandName("InvDrug");
        n.setMnn("MNN");
        n = nomenclatureRepository.save(n);

        batch = new Batch();
        batch.setNomenclature(n);
        batch.setBatchNumber("INV-1");
        batch.setExpiryDate(LocalDate.now().plusMonths(4));
        batch.setReceivedDate(LocalDate.now());
        batch.setQtyReceived(10);
        batch.setQtyInStock(0);
        batch = batchRepository.save(batch);
        stockService.receiveStock(pharmacy.getId(), batch, 10);
    }

    @Test
    void completeSessionAppliesShortage() {
        InventorySession session = inventoryService.startSession(pharmacy, user);
        InventoryLine line = session.getLines().getFirst();
        inventoryService.updateActualQuantities(session.getId(), Map.of(line.getId(), 8));
        inventoryService.completeSession(session.getId());

        Stock stock = stockRepository.findByPharmacyIdAndBatchId(pharmacy.getId(), batch.getId()).orElseThrow();
        assertEquals(8, stock.getQuantity());
        assertEquals("COMPLETED", inventoryService.findById(session.getId()).getStatus());
    }
}
