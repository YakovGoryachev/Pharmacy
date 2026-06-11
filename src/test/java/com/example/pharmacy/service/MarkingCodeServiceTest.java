package com.example.pharmacy.service;

import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Service.MarkingCodeService;
import com.example.pharmacy.exception.BusinessException;
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
class MarkingCodeServiceTest {

    @Autowired MarkingCodeService markingCodeService;
    @Autowired NomenclatureRepository nomenclatureRepository;
    @Autowired BatchRepository batchRepository;

    @Test
    void parseAndRegister() {
        Nomenclature n = new Nomenclature();
        n.setBrandName("Marked");
        n.setMarked(true);
        n = nomenclatureRepository.save(n);
        Batch b = new Batch();
        b.setNomenclature(n);
        b.setBatchNumber("M1");
        b.setExpiryDate(LocalDate.now().plusMonths(3));
        b = batchRepository.save(b);

        String code = "010460123456789021SERIAL001";
        var parsed = markingCodeService.parseCode(code);
        assertNotNull(parsed.gtin);
        assertNotNull(parsed.serial);

        var mc = markingCodeService.registerOnReceipt(code, n, b);
        assertEquals("IN_STOCK", mc.getStatus());
    }

    @Test
    void rejectEmptyCode() {
        assertThrows(BusinessException.class, () -> markingCodeService.parseCode("  "));
    }
}
