package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.StockRepository;
import com.example.pharmacy.Repository.StockTransferRepository;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final BatchRepository batchRepository;
    private final PharmacyRepository pharmacyRepository;
    private final StockTransferRepository stockTransferRepository;

    public StockService(StockRepository stockRepository,
                        BatchRepository batchRepository,
                        PharmacyRepository pharmacyRepository,
                        StockTransferRepository stockTransferRepository) {
        this.stockRepository = stockRepository;
        this.batchRepository = batchRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.stockTransferRepository = stockTransferRepository;
    }

    public void assertBatchNotExpired(Batch batch) {
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Партия просрочена: " + batch.getBatchNumber());
        }
    }

    @Transactional
    public Stock receiveStock(Long pharmacyId, Batch batch, int quantity) {
        assertBatchNotExpired(batch);
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new BusinessException("Аптека не найдена"));
        Stock stock = stockRepository.findByPharmacyIdAndBatchId(pharmacyId, batch.getId())
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setPharmacy(pharmacy);
                    s.setBatch(batch);
                    s.setQuantity(0);
                    s.setReserved(0);
                    return s;
                });
        stock.setQuantity(stock.getQuantity() + quantity);
        syncBatchQty(batch);
        return stockRepository.save(stock);
    }

    @Transactional
    public void reserve(Long pharmacyId, Long batchId, int qty) {
        Stock stock = getStock(pharmacyId, batchId);
        assertBatchNotExpired(stock.getBatch());
        if (stock.getAvailable() < qty) {
            throw new BusinessException("Недостаточно товара для резерва");
        }
        stock.setReserved(stock.getReserved() + qty);
        stockRepository.save(stock);
    }

    @Transactional
    public void releaseReserve(Long pharmacyId, Long batchId, int qty) {
        Stock stock = getStock(pharmacyId, batchId);
        stock.setReserved(Math.max(0, stock.getReserved() - qty));
        stockRepository.save(stock);
    }

    @Transactional
    public void commitSale(Long pharmacyId, Long batchId, int qty) {
        Stock stock = getStock(pharmacyId, batchId);
        assertBatchNotExpired(stock.getBatch());
        if (stock.getQuantity() < qty) {
            throw new BusinessException("Недостаточно товара на складе");
        }
        stock.setReserved(Math.max(0, stock.getReserved() - qty));
        stock.setQuantity(stock.getQuantity() - qty);
        stockRepository.save(stock);
        syncBatchQty(stock.getBatch());
    }

    @Transactional
    public void restoreFromReturn(Long pharmacyId, Long batchId, int qty) {
        Stock stock = stockRepository.findByPharmacyIdAndBatchId(pharmacyId, batchId)
                .orElseGet(() -> createEmptyStock(pharmacyId, batchId));
        stock.setQuantity(stock.getQuantity() + qty);
        stockRepository.save(stock);
        syncBatchQty(stock.getBatch());
    }

    @Transactional
    public void writeOff(Long pharmacyId, Long batchId, int qty) {
        Stock stock = getStock(pharmacyId, batchId);
        int available = stock.getAvailable();
        if (available < qty) {
            throw new BusinessException("Недостаточно доступного остатка для списания");
        }
        stock.setQuantity(stock.getQuantity() - qty);
        stockRepository.save(stock);
        Batch batch = stock.getBatch();
        syncBatchQty(batch);
        if (sumQuantityForBatch(batch.getId()) == 0) {
            batch.setWrittenOff(true);
            batchRepository.save(batch);
        }
    }

    @Transactional
    public void transfer(Long fromPharmacyId, Long toPharmacyId, Long batchId, int qty,
                         String waybillNumber, User user) {
        if (fromPharmacyId.equals(toPharmacyId)) {
            throw new BusinessException("Нельзя перемещать в ту же аптеку");
        }
        Stock from = getStock(fromPharmacyId, batchId);
        assertBatchNotExpired(from.getBatch());
        if (from.getAvailable() < qty) {
            throw new BusinessException("Недостаточно товара для перемещения");
        }
        from.setQuantity(from.getQuantity() - qty);
        stockRepository.save(from);
        Batch batch = from.getBatch();
        receiveStock(toPharmacyId, batch, qty);
        syncBatchQty(batch);

        StockTransfer transfer = new StockTransfer();
        transfer.setFromPharmacy(from.getPharmacy());
        transfer.setToPharmacy(pharmacyRepository.findById(toPharmacyId).orElseThrow());
        transfer.setBatch(batch);
        transfer.setQuantity(qty);
        transfer.setWaybillNumber(waybillNumber);
        transfer.setTransferDate(LocalDate.now());
        transfer.setUser(user);
        stockTransferRepository.save(transfer);
    }

    public Stock getStock(Long pharmacyId, Long batchId) {
        return stockRepository.findByPharmacyIdAndBatchId(pharmacyId, batchId)
                .orElseThrow(() -> new BusinessException("Остаток не найден для партии"));
    }

    public Stock getStockByBatchNumber(Long pharmacyId, String batchNumber) {
        Batch batch = batchRepository.findByBatchNumber(batchNumber.trim())
                .orElseThrow(() -> new BusinessException("Партия не найдена: " + batchNumber));
        return getStock(pharmacyId, batch.getId());
    }

    @Transactional
    public void transfer(Long fromPharmacyId, Long toPharmacyId, String batchNumber, int qty,
                         String waybillNumber, User user) {
        Batch batch = batchRepository.findByBatchNumber(batchNumber.trim())
                .orElseThrow(() -> new BusinessException("Партия не найдена: " + batchNumber));
        transfer(fromPharmacyId, toPharmacyId, batch.getId(), qty, waybillNumber, user);
    }

    @Transactional
    public void writeOff(Long pharmacyId, String batchNumber, int qty) {
        Batch batch = batchRepository.findByBatchNumber(batchNumber.trim())
                .orElseThrow(() -> new BusinessException("Партия не найдена: " + batchNumber));
        writeOff(pharmacyId, batch.getId(), qty);
    }

    public List<Stock> listByPharmacy(Long pharmacyId) {
        return stockRepository.findByPharmacyId(pharmacyId);
    }

    public List<Stock> findAvailableFefo(Long nomenclatureId) {
        return stockRepository.findAvailableByNomenclatureFefo(nomenclatureId);
    }

    public int totalAvailableInPharmacy(Long pharmacyId, Long nomenclatureId) {
        return stockRepository.sumAvailableForSale(pharmacyId, nomenclatureId, LocalDate.now());
    }

    public Stock pickFefoStock(Long pharmacyId, Long nomenclatureId, int qty) {
        for (Stock s : stockRepository.findAvailableByNomenclatureFefo(nomenclatureId)) {
            if (!s.getPharmacy().getId().equals(pharmacyId)) {
                continue;
            }
            assertBatchNotExpired(s.getBatch());
            if (s.getAvailable() >= qty) {
                return s;
            }
        }
        throw new BusinessException("Нет доступной партии (FEFO) для выбранного количества");
    }

    public List<Stock> findExpiring(Long pharmacyId, int days) {
        LocalDate before = LocalDate.now().plusDays(days);
        if (pharmacyId == null) {
            return stockRepository.findExpiringNetwork(before);
        }
        return stockRepository.findExpiring(pharmacyId, before);
    }

    public List<Stock> findOtherPharmaciesStock(Long nomenclatureId, Long excludePharmacyId) {
        return stockRepository.findAvailableByNomenclatureFefo(nomenclatureId).stream()
                .filter(s -> !s.getPharmacy().getId().equals(excludePharmacyId))
                .toList();
    }

    private Stock createEmptyStock(Long pharmacyId, Long batchId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow();
        Batch batch = batchRepository.findById(batchId).orElseThrow();
        Stock stock = new Stock();
        stock.setPharmacy(pharmacy);
        stock.setBatch(batch);
        stock.setQuantity(0);
        stock.setReserved(0);
        return stockRepository.save(stock);
    }

    private void syncBatchQty(Batch batch) {
        int total = sumQuantityForBatch(batch.getId());
        batch.setQtyInStock(total);
        batchRepository.save(batch);
    }

    private int sumQuantityForBatch(Long batchId) {
        return stockRepository.sumQuantityByBatchId(batchId);
    }
}
