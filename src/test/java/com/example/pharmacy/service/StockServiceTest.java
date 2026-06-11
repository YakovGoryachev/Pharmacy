package com.example.pharmacy.service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.*;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.exception.BusinessException;
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
class StockServiceTest {

    @Autowired StockService stockService;
    @Autowired PharmacyRepository pharmacyRepository;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;
    @Autowired StockRepository stockRepository;

    private Pharmacy pharmacy;
    private Batch batch;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy();
        pharmacy.setName("Test");
        pharmacy.setActive(true);
        pharmacy = pharmacyRepository.save(pharmacy);

        Nomenclature n = new Nomenclature();
        n.setBrandName("TestDrug");
        n.setMnn("MNN");
        n.setMarked(false);
        n = nomenclatureRepository.save(n);

        batch = new Batch();
        batch.setNomenclature(n);
        batch.setBatchNumber("T-1");
        batch.setExpiryDate(LocalDate.now().plusMonths(6));
        batch.setReceivedDate(LocalDate.now());
        batch.setQtyReceived(10);
        batch.setQtyInStock(0);
        batch = batchRepository.save(batch);
    }

    @Test
    void receiveAndReserve() {
        stockService.receiveStock(pharmacy.getId(), batch, 10);
        stockService.reserve(pharmacy.getId(), batch.getId(), 3);
        Stock s = stockRepository.findByPharmacyIdAndBatchId(pharmacy.getId(), batch.getId()).orElseThrow();
        assertEquals(10, s.getQuantity());
        assertEquals(3, s.getReserved());
        assertEquals(7, s.getAvailable());
    }

    @Test
    void blockExpiredBatch() {
        batch.setExpiryDate(LocalDate.now().minusDays(1));
        batchRepository.save(batch);
        assertThrows(BusinessException.class, () -> stockService.receiveStock(pharmacy.getId(), batch, 1));
    }
}
