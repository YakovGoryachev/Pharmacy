package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.WriteOffDocumentRepository;
import com.example.pharmacy.audit.Audited;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WriteOffService {

    private final WriteOffDocumentRepository writeOffDocumentRepository;
    private final StockService stockService;
    private final PharmacyRepository pharmacyRepository;
    private final BatchRepository batchRepository;

    public WriteOffService(WriteOffDocumentRepository writeOffDocumentRepository,
                           StockService stockService,
                           PharmacyRepository pharmacyRepository,
                           BatchRepository batchRepository) {
        this.writeOffDocumentRepository = writeOffDocumentRepository;
        this.stockService = stockService;
        this.pharmacyRepository = pharmacyRepository;
        this.batchRepository = batchRepository;
    }

    @Transactional
    @Audited(entity = "WriteOffDocument", action = "WRITE_OFF")
    public WriteOffDocument writeOff(Long pharmacyId, String batchNumber, int quantity,
                                     WriteOffReason reason, String comment, User user) {
        if (quantity <= 0) {
            throw new BusinessException("Количество должно быть больше нуля");
        }
        Batch batch = batchRepository.findByBatchNumber(batchNumber.trim())
                .orElseThrow(() -> new BusinessException("Партия не найдена: " + batchNumber));
        stockService.writeOff(pharmacyId, batch.getId(), quantity);
        WriteOffDocument doc = new WriteOffDocument();
        doc.setPharmacy(pharmacyRepository.findById(pharmacyId).orElseThrow());
        doc.setBatch(batch);
        doc.setQuantity(quantity);
        doc.setReason(reason);
        doc.setComment(comment);
        doc.setUser(user);
        return writeOffDocumentRepository.save(doc);
    }
}
