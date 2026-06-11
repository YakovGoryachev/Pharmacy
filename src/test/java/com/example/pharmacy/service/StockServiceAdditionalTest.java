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
class StockServiceAdditionalTest {

    @Autowired StockService stockService;
    @Autowired PharmacyRepository pharmacyRepository;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;

    private Pharmacy pharmacy;
    private Nomenclature nomenclature;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy();
        pharmacy.setName("FEFO Test");
        pharmacy.setActive(true);
        pharmacy = pharmacyRepository.save(pharmacy);

        nomenclature = new Nomenclature();
        nomenclature.setBrandName("FefoDrug");
        nomenclature.setMnn("MNN");
        nomenclature = nomenclatureRepository.save(nomenclature);
    }

    @Test
    void pickFefoChoosesEarliestExpiry() {
        Batch later = new Batch();
        later.setNomenclature(nomenclature);
        later.setBatchNumber("L");
        later.setExpiryDate(LocalDate.now().plusMonths(6));
        later.setReceivedDate(LocalDate.now());
        later.setQtyReceived(5);
        later.setQtyInStock(0);
        later = batchRepository.save(later);
        stockService.receiveStock(pharmacy.getId(), later, 5);

        Batch earlier = new Batch();
        earlier.setNomenclature(nomenclature);
        earlier.setBatchNumber("E");
        earlier.setExpiryDate(LocalDate.now().plusMonths(2));
        earlier.setReceivedDate(LocalDate.now());
        earlier.setQtyReceived(5);
        earlier.setQtyInStock(0);
        earlier = batchRepository.save(earlier);
        stockService.receiveStock(pharmacy.getId(), earlier, 5);

        Stock picked = stockService.pickFefoStock(pharmacy.getId(), nomenclature.getId(), 1);
        assertEquals(earlier.getExpiryDate(), picked.getBatch().getExpiryDate());
    }

    @Test
    void cannotReserveMoreThanAvailable() {
        Batch batch = new Batch();
        batch.setNomenclature(nomenclature);
        batch.setBatchNumber("R");
        batch.setExpiryDate(LocalDate.now().plusMonths(3));
        batch.setReceivedDate(LocalDate.now());
        batch.setQtyReceived(2);
        batch.setQtyInStock(0);
        final Batch saved = batchRepository.save(batch);
        stockService.receiveStock(pharmacy.getId(), saved, 2);
        assertThrows(BusinessException.class, () -> stockService.reserve(pharmacy.getId(), saved.getId(), 5));
    }
}
