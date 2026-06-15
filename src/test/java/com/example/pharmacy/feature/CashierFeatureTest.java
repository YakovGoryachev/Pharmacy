package com.example.pharmacy.feature;

import com.example.pharmacy.DTO.CartItemDto;
import com.example.pharmacy.DTO.PrescriptionFormDto;
import com.example.pharmacy.Pojo.*;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.ChequeRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Service.ChequeService;
import com.example.pharmacy.Service.MarkingCodeService;
import com.example.pharmacy.Service.StockService;
import com.example.pharmacy.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("АРМ кассира")
class CashierFeatureTest {

    @Mock ChequeRepository chequeRepository;
    @Mock NomenclatureRepository nomenclatureRepository;
    @Mock StockService stockService;
    @Mock MarkingCodeService markingCodeService;
    @Mock PharmacyRepository pharmacyRepository;
    @Mock BatchRepository batchRepository;

    @InjectMocks ChequeService chequeService;

    Pharmacy pharmacy;
    User cashier;
    Nomenclature product;
    Batch batch;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy();
        pharmacy.setId(1L);

        cashier = new User();
        cashier.setId(2L);

        product = new Nomenclature();
        product.setId(100L);
        product.setBrandName("Нурофен");

        batch = new Batch();
        batch.setId(10L);
        batch.setBatchNumber("П-001");
    }

    @Nested
    @DisplayName("CRUD")
    class Crud {

        @Test
        @DisplayName("Create — оформление чека (продажа)")
        void createSale() {
            CartItemDto item = item(2, 15000);

            when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
            when(nomenclatureRepository.findById(100L)).thenReturn(Optional.of(product));
            when(batchRepository.findById(10L)).thenReturn(Optional.of(batch));
            when(chequeRepository.save(any())).thenAnswer(inv -> {
                Cheque c = inv.getArgument(0);
                c.setId(99L);
                return c;
            });

            Cheque cheque = chequeService.checkout(1L, cashier, PaymentMethod.CASH, List.of(item));

            assertNotNull(cheque.getNumberCheque());
            assertEquals(30000, cheque.getTotalAmount());
            assertFalse(cheque.getIsReturned());
            verify(chequeRepository).save(any());
            verify(stockService).commitSale(1L, 10L, 2);
        }

        @Test
        @DisplayName("Read — расчёт суммы позиции в корзине")
        void readLineTotal() {
            CartItemDto item = item(3, 15000);
            assertEquals(45000, item.getLineTotal());
        }

        @Test
        @DisplayName("Update — возврат товара по чеку")
        void updateReturn() {
            Cheque cheque = chequeForReturn();
            when(chequeRepository.findById(5L)).thenReturn(Optional.of(cheque));
            when(chequeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Cheque result = chequeService.returnCheque(5L);

            assertTrue(result.getIsReturned());
            verify(stockService).restoreFromReturn(1L, 10L, 2);
            verify(markingCodeService).returnToStock(any());
        }
    }

    @Nested
    @DisplayName("Исключительные ситуации")
    class Exceptions {

        @Test
        @DisplayName("Create — пустая корзина")
        void emptyCart() {
            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(1L, cashier, PaymentMethod.CASH, List.of()));
        }

        @Test
        @DisplayName("Create — корзина null")
        void nullCart() {
            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(1L, cashier, PaymentMethod.CASH, null));
        }

        @Test
        @DisplayName("Create — аптека не найдена")
        void pharmacyNotFound() {
            when(pharmacyRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(999L, cashier, PaymentMethod.CASH, List.of(item(1, 1000))));
        }

        @Test
        @DisplayName("Create — рецептурный без данных рецепта")
        void prescriptionRequired() {
            CartItemDto item = item(1, 50000);
            item.setReceiptRequired(true);
            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(1L, cashier, PaymentMethod.CASH, List.of(item)));
        }

        @Test
        @DisplayName("Create — рецепт заполнен не полностью")
        void prescriptionIncomplete() {
            CartItemDto item = item(1, 50000);
            item.setReceiptRequired(true);
            PrescriptionFormDto rx = new PrescriptionFormDto();
            rx.setPrescriptionNumber("RX-1");
            item.setPrescription(rx);

            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(1L, cashier, PaymentMethod.CASH, List.of(item)));
        }

        @Test
        @DisplayName("Create — маркированный товар без кода")
        void markedWithoutCode() {
            CartItemDto item = item(1, 20000);
            item.setMarked(true);
            assertThrows(BusinessException.class,
                    () -> chequeService.checkout(1L, cashier, PaymentMethod.CASH, List.of(item)));
        }

        @Test
        @DisplayName("Update — повторный возврат чека")
        void returnAlreadyReturned() {
            Cheque cheque = new Cheque();
            cheque.setIsReturned(true);
            when(chequeRepository.findById(5L)).thenReturn(Optional.of(cheque));

            assertThrows(BusinessException.class, () -> chequeService.returnCheque(5L));
        }

        @Test
        @DisplayName("Update — чек для возврата не найден")
        void returnChequeNotFound() {
            when(chequeRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> chequeService.returnCheque(999L));
        }
    }

    private CartItemDto item(int qty, int priceKopecks) {
        CartItemDto item = new CartItemDto();
        item.setNomenclatureId(100L);
        item.setBatchId(10L);
        item.setQuantity(qty);
        item.setPrice(priceKopecks);
        item.setDisplayName("Нурофен");
        return item;
    }

    private Cheque chequeForReturn() {
        Cheque cheque = new Cheque();
        cheque.setId(5L);
        cheque.setIsReturned(false);
        cheque.setPharmacy(pharmacy);

        ChequePosition pos = new ChequePosition();
        pos.setCheque(cheque);
        pos.setBatch(batch);
        pos.setQuantity(2);
        pos.setMarkingCode(new MarkingCode());

        cheque.setChequePositions(new ArrayList<>(List.of(pos)));
        return cheque;
    }
}
