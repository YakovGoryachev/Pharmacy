package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.InventorySessionRepository;
import com.example.pharmacy.Repository.StockRepository;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InventoryService {

    private final InventorySessionRepository inventorySessionRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    public InventoryService(InventorySessionRepository inventorySessionRepository,
                            StockRepository stockRepository,
                            StockService stockService) {
        this.inventorySessionRepository = inventorySessionRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    @Transactional
    public InventorySession startSession(Pharmacy pharmacy, User user) {
        InventorySession session = new InventorySession();
        session.setPharmacy(pharmacy);
        session.setUser(user);
        session.setStatus("OPEN");
        List<Stock> stocks = stockRepository.findByPharmacyId(pharmacy.getId());
        for (Stock stock : stocks) {
            InventoryLine line = new InventoryLine();
            line.setSession(session);
            line.setStock(stock);
            line.setBookQuantity(stock.getQuantity());
            line.setActualQuantity(stock.getQuantity());
            session.getLines().add(line);
        }
        return inventorySessionRepository.save(session);
    }

    @Transactional
    public InventorySession completeSession(Long sessionId) {
        InventorySession session = inventorySessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("Сессия инвентаризации не найдена"));
        Long pharmacyId = session.getPharmacy().getId();
        for (InventoryLine line : session.getLines()) {
            int diff = line.getDifference();
            if (diff == 0) {
                continue;
            }
            Stock stock = line.getStock();
            Long batchId = stock.getBatch().getId();
            if (diff < 0) {
                stockService.writeOff(pharmacyId, batchId, -diff);
            } else {
                stockService.restoreFromReturn(pharmacyId, batchId, diff);
            }
        }
        session.setStatus("COMPLETED");
        session.setCompletedAt(Instant.now());
        return inventorySessionRepository.save(session);
    }

    public InventorySession findById(Long id) {
        return inventorySessionRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void updateActualQuantities(Long sessionId, java.util.Map<Long, Integer> actualByLineId) {
        InventorySession session = findById(sessionId);
        if (!"OPEN".equals(session.getStatus())) {
            throw new BusinessException("Сессия уже закрыта");
        }
        for (InventoryLine line : session.getLines()) {
            Integer actual = actualByLineId.get(line.getId());
            if (actual != null) {
                line.setActualQuantity(actual);
            }
        }
        inventorySessionRepository.save(session);
    }

    public List<InventorySession> listByPharmacy(Long pharmacyId) {
        return inventorySessionRepository.findByPharmacyIdOrderByCreatedAtDesc(pharmacyId);
    }
}
