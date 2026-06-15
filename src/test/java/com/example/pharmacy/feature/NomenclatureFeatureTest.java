package com.example.pharmacy.feature;

import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Pojo.ProductType;
import com.example.pharmacy.Repository.AtcManualRepository;
import com.example.pharmacy.Repository.CategoryRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Service.NomenclatureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модуль номенклатуры")
class NomenclatureFeatureTest {

    @Mock NomenclatureRepository nomenclatureRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock AtcManualRepository atcManualRepository;

    @InjectMocks NomenclatureService nomenclatureService;

    @Nested
    @DisplayName("CRUD")
    class Crud {

        @Test
        @DisplayName("Create — создание новой карточки препарата")
        void create() {
            NomenclatureDto dto = dto(null, "Нурофен", "Ибупрофен");

            when(nomenclatureRepository.save(any())).thenAnswer(inv -> {
                Nomenclature saved = inv.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            Nomenclature result = nomenclatureService.save(dto);

            assertEquals(1L, result.getId());
            assertEquals("Нурофен", result.getBrandName());
            assertNull(result.getPrice());
            verify(nomenclatureRepository).save(any());
        }

        @Test
        @DisplayName("Read — чтение карточки по id")
        void read() {
            Nomenclature entity = entity(5L, "Аспирин", "Ацетилсалициловая кислота");
            when(nomenclatureRepository.findById(5L)).thenReturn(Optional.of(entity));

            NomenclatureDto dto = nomenclatureService.findById(5L);

            assertEquals(5L, dto.getId());
            assertEquals("Аспирин", dto.getBrandName());
        }

        @Test
        @DisplayName("Update — редактирование названия и МНН")
        void update() {
            Nomenclature existing = entity(5L, "Старое", "Старое МНН");
            existing.setPrice(9999);

            NomenclatureDto dto = dto(5L, "Новое", "Новое МНН");

            when(nomenclatureRepository.findById(5L)).thenReturn(Optional.of(existing));
            when(nomenclatureRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Nomenclature result = nomenclatureService.save(dto);

            assertEquals("Новое", result.getBrandName());
            assertEquals("Новое МНН", result.getMnn());
            assertEquals(9999, result.getPrice());
        }

        @Test
        @DisplayName("Delete — удаление позиции")
        void delete() {
            nomenclatureService.delete(5L);
            verify(nomenclatureRepository).deleteById(5L);
        }
    }

    @Nested
    @DisplayName("Исключительные ситуации")
    class Exceptions {

        @Test
        @DisplayName("Read — карточка не найдена")
        void readNotFound() {
            when(nomenclatureRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> nomenclatureService.findById(999L));
        }

        @Test
        @DisplayName("Search — пустой поисковый запрос")
        void emptySearch() {
            assertTrue(nomenclatureService.searchByNameOrMnn("   ", 10).isEmpty());
            verifyNoInteractions(nomenclatureRepository);
        }

        @Test
        @DisplayName("Search — null вместо запроса")
        void nullSearch() {
            assertTrue(nomenclatureService.searchByNameOrMnn(null, 10).isEmpty());
        }

        @Test
        @DisplayName("Delete — после удаления карточка недоступна")
        void notFoundAfterDelete() {
            when(nomenclatureRepository.findById(5L)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> nomenclatureService.findById(5L));
        }
    }

    private static NomenclatureDto dto(Long id, String brand, String mnn) {
        NomenclatureDto dto = new NomenclatureDto();
        dto.setId(id);
        dto.setBrandName(brand);
        dto.setMnn(mnn);
        dto.setProductType(ProductType.MEDICINE.name());
        return dto;
    }

    private static Nomenclature entity(Long id, String brand, String mnn) {
        Nomenclature nm = new Nomenclature();
        nm.setId(id);
        nm.setBrandName(brand);
        nm.setMnn(mnn);
        nm.setProductType(ProductType.MEDICINE.name());
        nm.setMarked(false);
        return nm;
    }
}
