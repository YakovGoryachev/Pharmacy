package com.example.pharmacy.service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.Service.WriteOffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WriteOffServiceTest {

    @Autowired WriteOffService writeOffService;
    @Autowired StockService stockService;
    @Autowired PharmacyRepository pharmacyRepository;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired StockRepository stockRepository;
    @Autowired UserRepository userRepository;

    private Pharmacy pharmacy;
    private User user;
    private Batch batch;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy();
        pharmacy.setName("WO Test");
        pharmacy.setActive(true);
        pharmacy = pharmacyRepository.save(pharmacy);

        user = new User();
        user.setLogin("wo-user");
        user.setPassword("x");
        user.setName("WO");
        user = userRepository.save(user);

        Nomenclature n = new Nomenclature();
        n.setBrandName("WODrug");
        n.setMnn("MNN");
        n = nomenclatureRepository.save(n);

        batch = new Batch();
        batch.setNomenclature(n);
        batch.setBatchNumber("WO-1");
        batch.setExpiryDate(LocalDate.now().plusMonths(2));
        batch.setReceivedDate(LocalDate.now());
        batch.setQtyReceived(10);
        batch.setQtyInStock(0);
        batch = batchRepository.save(batch);
        stockService.receiveStock(pharmacy.getId(), batch, 10);
    }

    @Test
    void writeOffReducesStock() {
        writeOffService.writeOff(pharmacy.getId(), batch.getId(), 4, WriteOffReason.DAMAGE, "broken", user);
        Stock stock = stockRepository.findByPharmacyIdAndBatchId(pharmacy.getId(), batch.getId()).orElseThrow();
        assertEquals(6, stock.getQuantity());
    }
}
