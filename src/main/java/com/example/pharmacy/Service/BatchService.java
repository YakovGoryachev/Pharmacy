package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Specifications.BatchSpecifications;
import com.example.pharmacy.audit.Audited;
import com.example.pharmacy.exception.BusinessException;
import com.example.pharmacy.util.MoneyUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final NomenclatureRepository nomenclatureRepository;
    private final StockService stockService;

    public BatchService(BatchRepository batchRepository,
                        NomenclatureRepository nomenclatureRepository,
                        StockService stockService) {
        this.batchRepository = batchRepository;
        this.nomenclatureRepository = nomenclatureRepository;
        this.stockService = stockService;
    }

    public Page<Batch> findFilteredBatches(String numBatches, String supplier, LocalDate dateEntrance,
                                           LocalDate expiryDate, int page, int size, String sort, String dir) {
        Specification<Batch> spec = BatchSpecifications.hasFilters(numBatches, supplier, dateEntrance, expiryDate);
        Sort s = "asc".equalsIgnoreCase(dir) ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, s);
        return batchRepository.findAll(spec, pageable);
    }

    @Transactional
    @Audited(entity = "Batch", action = "RECEIVE")
    public Batch saveWithStock(BatchDto bdto, Long pharmacyId) {
        assertUniqueBatchNumber(bdto.getBatchNumber(), null);
        Batch batch = mapToPojo(bdto);
        try {
            batch = batchRepository.save(batch);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Партия с номером «" + bdto.getBatchNumber().trim()
                    + "» уже есть в системе");
        }
        int qty = bdto.getQtyReceived() != null ? bdto.getQtyReceived() : bdto.getQtyInStock();
        if (qty > 0 && pharmacyId != null) {
            stockService.receiveStock(pharmacyId, batch, qty);
        }
        return batch;
    }

    @Transactional
    public Batch update(BatchDto bdto) {
        Batch batch = batchRepository.findById(bdto.getId()).orElseThrow();
        assertUniqueBatchNumber(bdto.getBatchNumber(), bdto.getId());
        Nomenclature n = nomenclatureRepository.findById(bdto.getNomenclatureId()).orElseThrow();
        batch.setNomenclature(n);
        batch.setBatchNumber(bdto.getBatchNumber());
        batch.setExpiryDate(bdto.getExpiryDate());
        batch.setProductionDate(bdto.getProductionDate());
        batch.setReceivedDate(bdto.getReceivedDate());
        batch.setSupplier(bdto.getSupplier());
        batch.setPrice(resolvePriceKopecks(bdto));
        batch.setStorageZone(bdto.getStorageZone());
        batch.setWrittenOff(bdto.getWrittenOff());
        return batchRepository.save(batch);
    }

    public BatchDto findById(Long id) {
        return batchRepository.findById(id).map(this::mapToDto).orElse(null);
    }

    public void deleteById(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Партия не найдена"));
        if (!Boolean.TRUE.equals(batch.getWrittenOff())) {
            throw new BusinessException("Удалить можно только списанную партию. Для остатков используйте списание.");
        }
        batchRepository.delete(batch);
    }

    private void assertUniqueBatchNumber(String batchNumber, Long excludeId) {
        if (batchNumber == null || batchNumber.isBlank()) {
            return;
        }
        batchRepository.findByBatchNumber(batchNumber.trim()).ifPresent(existing -> {
            if (excludeId == null || !excludeId.equals(existing.getId())) {
                throw new BusinessException("Партия с номером «" + batchNumber.trim() + "» уже есть в системе");
            }
        });
    }

    public List<Batch> findExpiringBatches(LocalDate before) {
        return batchRepository.findAll().stream()
                .filter(b -> b.getExpiryDate() != null && !b.getExpiryDate().isAfter(before))
                .filter(b -> b.getQtyInStock() != null && b.getQtyInStock() > 0)
                .toList();
    }

    private Batch mapToPojo(BatchDto bdto) {
        Batch b = new Batch();
        if (bdto.getId() != null) {
            b.setId(bdto.getId());
        }
        Nomenclature n = nomenclatureRepository.findById(bdto.getNomenclatureId()).orElseThrow();
        b.setNomenclature(n);
        b.setBatchNumber(bdto.getBatchNumber());
        b.setExpiryDate(bdto.getExpiryDate());
        b.setProductionDate(bdto.getProductionDate());
        b.setReceivedDate(bdto.getReceivedDate() != null ? bdto.getReceivedDate() : LocalDate.now());
        b.setSupplier(bdto.getSupplier());
        b.setPrice(resolvePriceKopecks(bdto));
        int qty = bdto.getQtyReceived() != null ? bdto.getQtyReceived() : (bdto.getQtyInStock() != null ? bdto.getQtyInStock() : 0);
        b.setQtyReceived(qty);
        b.setQtyInStock(0);
        b.setStorageZone(bdto.getStorageZone());
        b.setWrittenOff(false);
        return b;
    }

    private BatchDto mapToDto(Batch b) {
        BatchDto bdto = new BatchDto();
        bdto.setId(b.getId());
        bdto.setNomenclatureId(b.getNomenclature().getId());
        bdto.setBatchNumber(b.getBatchNumber());
        bdto.setExpiryDate(b.getExpiryDate());
        bdto.setProductionDate(b.getProductionDate());
        bdto.setReceivedDate(b.getReceivedDate());
        bdto.setSupplier(b.getSupplier());
        bdto.setPrice(b.getPrice());
        bdto.setPriceRubles(MoneyUtils.toRubles(b.getPrice()));
        bdto.setQtyReceived(b.getQtyReceived());
        bdto.setQtyInStock(b.getQtyInStock());
        bdto.setStorageZone(b.getStorageZone());
        bdto.setWrittenOff(b.getWrittenOff());
        return bdto;
    }

    private static Integer resolvePriceKopecks(BatchDto bdto) {
        return MoneyUtils.resolveKopecks(bdto.getPriceRubles(), bdto.getPrice());
    }
}
