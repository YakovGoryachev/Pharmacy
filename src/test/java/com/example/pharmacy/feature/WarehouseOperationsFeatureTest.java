package com.example.pharmacy.feature;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.StockRepository;
import com.example.pharmacy.Repository.StockTransferRepository;
import com.example.pharmacy.Repository.WriteOffDocumentRepository;
import com.example.pharmacy.Service.BatchService;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.Service.WriteOffService;
import com.example.pharmacy.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модуль складских операций")
class WarehouseOperationsFeatureTest {

    @Mock StockRepository stockRepository;
    @Mock BatchRepository batchRepository;
    @Mock PharmacyRepository pharmacyRepository;
    @Mock StockTransferRepository stockTransferRepository;
    @Mock WriteOffDocumentRepository writeOffDocumentRepository;
    @Mock NomenclatureRepository nomenclatureRepository;

    StockService stockService;
    BatchService batchService;
    WriteOffService writeOffService;

    Pharmacy pharmacy;
    Pharmacy pharmacy2;
    Nomenclature nomenclature;
    Batch batch;
    Stock stock;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository, batchRepository, pharmacyRepository, stockTransferRepository);
        batchService = new BatchService(batchRepository, nomenclatureRepository, stockService);
        writeOffService = new WriteOffService(writeOffDocumentRepository, stockService, pharmacyRepository, batchRepository);

        pharmacy = new Pharmacy();
        pharmacy.setId(1L);
        pharmacy2 = new Pharmacy();
        pharmacy2.setId(2L);

        nomenclature = new Nomenclature();
        nomenclature.setId(100L);

        batch = new Batch();
        batch.setId(10L);
        batch.setBatchNumber("П-001");
        batch.setExpiryDate(LocalDate.now().plusMonths(6));

        stock = new Stock();
        stock.setPharmacy(pharmacy);
        stock.setBatch(batch);
        stock.setQuantity(10);
        stock.setReserved(0);
    }

    @Nested
    @DisplayName("CRUD")
    class Crud {

        @Test
        @DisplayName("Create — приёмка партии на склад")
        void createReceipt() {
            BatchDto dto = batchDto(10);

            when(batchRepository.findByBatchNumber("П-001")).thenReturn(Optional.empty());
            when(nomenclatureRepository.findById(100L)).thenReturn(Optional.of(nomenclature));
            when(batchRepository.save(any())).thenAnswer(inv -> {
                Batch b = inv.getArgument(0);
                b.setId(10L);
                return b;
            });
            when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.empty());
            when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(stockRepository.sumQuantityByBatchId(10L)).thenReturn(10);

            Batch saved = batchService.saveWithStock(dto, 1L);

            assertEquals("П-001", saved.getBatchNumber());
            verify(stockRepository).save(argThat(s -> s.getQuantity() == 10));
        }

        @Test
        @DisplayName("Create — дубликат номера партии запрещён")
        void createDuplicateBatchNumber() {
            BatchDto dto = batchDto(10);
            when(batchRepository.findByBatchNumber("П-001")).thenReturn(Optional.of(batch));

            assertThrows(BusinessException.class, () -> batchService.saveWithStock(dto, 1L));
        }

        @Test
        @DisplayName("Delete — удаление только списанной партии")
        void deleteOnlyWrittenOff() {
            batch.setWrittenOff(false);
            when(batchRepository.findById(10L)).thenReturn(Optional.of(batch));

            assertThrows(BusinessException.class, () -> batchService.deleteById(10L));
            verify(batchRepository, never()).delete(any(Batch.class));
        }

        @Test
        @DisplayName("Read — получение остатка по партии")
        void readStock() {
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));

            Stock result = stockService.getStock(1L, 10L);

            assertEquals(10, result.getQuantity());
            assertEquals(10, result.getAvailable());
        }

        @Test
        @DisplayName("Read — список остатков аптеки")
        void readStockList() {
            when(stockRepository.findByPharmacyId(1L)).thenReturn(List.of(stock));

            List<Stock> list = stockService.listByPharmacy(1L);

            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Update — резервирование и проведение продажи")
        void updateReserveAndCommit() {
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(stockRepository.sumQuantityByBatchId(10L)).thenReturn(8);

            stockService.reserve(1L, 10L, 2);
            assertEquals(2, stock.getReserved());

            stockService.commitSale(1L, 10L, 2);
            assertEquals(8, stock.getQuantity());
            assertEquals(0, stock.getReserved());
        }

        @Test
        @DisplayName("Update — перемещение между аптеками")
        void updateTransfer() {
            when(batchRepository.findByBatchNumber("П-001")).thenReturn(Optional.of(batch));
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            when(stockRepository.findByPharmacyIdAndBatchId(2L, 10L)).thenReturn(Optional.empty());
            when(pharmacyRepository.findById(2L)).thenReturn(Optional.of(pharmacy2));
            when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(stockRepository.sumQuantityByBatchId(10L)).thenReturn(5);
            when(batchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(stockTransferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            stockService.transfer(1L, 2L, "П-001", 5, "ТТН-1", new User());

            assertEquals(5, stock.getQuantity());
            verify(stockTransferRepository).save(any());
        }

        @Test
        @DisplayName("Delete — списание товара со склада")
        void deleteWriteOff() {
            User user = new User();
            when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
            when(batchRepository.findByBatchNumber("П-001")).thenReturn(Optional.of(batch));
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            when(stockRepository.sumQuantityByBatchId(10L)).thenReturn(7);
            when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(batchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(writeOffDocumentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            WriteOffDocument doc = writeOffService.writeOff(1L, "П-001", 3, WriteOffReason.DAMAGE, "Бой", user);

            assertEquals(3, doc.getQuantity());
            assertEquals(7, stock.getQuantity());
        }
    }

    @Nested
    @DisplayName("Исключительные ситуации")
    class Exceptions {

        @Test
        @DisplayName("Read — остаток не найден")
        void stockNotFound() {
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.empty());
            assertThrows(BusinessException.class, () -> stockService.getStock(1L, 10L));
        }

        @Test
        @DisplayName("Create — приёмка просроченной партии запрещена")
        void receiptExpiredBatch() {
            batch.setExpiryDate(LocalDate.now().minusDays(1));
            assertThrows(BusinessException.class, () -> stockService.receiveStock(1L, batch, 5));
        }

        @Test
        @DisplayName("Update — резерв больше доступного остатка")
        void reserveTooMuch() {
            stock.setQuantity(2);
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            assertThrows(BusinessException.class, () -> stockService.reserve(1L, 10L, 5));
        }

        @Test
        @DisplayName("Update — перемещение в ту же аптеку")
        void transferToSamePharmacy() {
            assertThrows(BusinessException.class,
                    () -> stockService.transfer(1L, 1L, 10L, 1, "ТТН", new User()));
        }

        @Test
        @DisplayName("Update — перемещение при нехватке товара")
        void transferInsufficientStock() {
            stock.setQuantity(2);
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            assertThrows(BusinessException.class,
                    () -> stockService.transfer(1L, 2L, 10L, 5, "ТТН", new User()));
        }

        @Test
        @DisplayName("Read — FEFO: нет подходящей партии")
        void fefoNotFound() {
            when(stockRepository.findAvailableByNomenclatureFefo(100L)).thenReturn(List.of());
            assertThrows(BusinessException.class, () -> stockService.pickFefoStock(1L, 100L, 1));
        }

        @Test
        @DisplayName("Delete — списание нулевого или отрицательного количества")
        void writeOffInvalidQty() {
            User user = new User();
            assertThrows(BusinessException.class,
                    () -> writeOffService.writeOff(1L, "П-001", 0, WriteOffReason.EXPIRED, "", user));
            assertThrows(BusinessException.class,
                    () -> writeOffService.writeOff(1L, "П-001", -2, WriteOffReason.EXPIRED, "", user));
        }

        @Test
        @DisplayName("Delete — списание больше доступного остатка")
        void writeOffTooMuch() {
            stock.setReserved(8);
            when(stockRepository.findByPharmacyIdAndBatchId(1L, 10L)).thenReturn(Optional.of(stock));
            assertThrows(BusinessException.class, () -> stockService.writeOff(1L, 10L, 5));
        }
    }

    private BatchDto batchDto(int qty) {
        BatchDto dto = new BatchDto();
        dto.setNomenclatureId(100L);
        dto.setBatchNumber("П-001");
        dto.setExpiryDate(LocalDate.now().plusMonths(6));
        dto.setQtyReceived(qty);
        dto.setPriceRubles(100.0);
        return dto;
    }
}
