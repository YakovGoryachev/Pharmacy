package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.Pojo.NomenclatureCategory;
import com.example.pharmacy.Pojo.RoleName;
import com.example.pharmacy.Repository.CategoryRepository;
import com.example.pharmacy.exception.BusinessException;
import com.example.pharmacy.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<NomenclatureCategoryDto> search(String query) {
        return categoryRepository.findByNameContainingIgnoreCase(query)
                .stream().limit(10).map(this::mapToDto).collect(Collectors.toList());
    }

    /** Создание при автодополнении в форме номенклатуры — всегда несистемная. */
    @Transactional
    public NomenclatureCategoryDto getOrCreate(String name) {
        return categoryRepository.findByNameIgnoreCase(name.trim())
                .map(this::mapToDto)
                .orElseGet(() -> {
                    NomenclatureCategory cat = new NomenclatureCategory();
                    cat.setName(name.trim());
                    cat.setSystem(false);
                    return mapToDto(categoryRepository.save(cat));
                });
    }

    public List<NomenclatureCategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NomenclatureCategoryDto save(NomenclatureCategoryDto dto, boolean markAsSystem) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("Укажите название категории");
        }
        boolean admin = canManageSystemCategories();
        if (dto.getId() != null) {
            NomenclatureCategory existing = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new BusinessException("Категория не найдена"));
            if (existing.isSystem() && !admin) {
                throw new BusinessException("Системную категорию может изменять только администратор");
            }
            existing.setName(dto.getName().trim());
            if (dto.getCode() != null && !dto.getCode().isBlank()) {
                existing.setCode(dto.getCode().trim());
            }
            if (admin) {
                existing.setSystem(markAsSystem);
            }
            return mapToDto(categoryRepository.save(existing));
        }
        NomenclatureCategory cat = new NomenclatureCategory();
        cat.setName(dto.getName().trim());
        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            cat.setCode(dto.getCode().trim());
        }
        cat.setSystem(admin && markAsSystem);
        return mapToDto(categoryRepository.save(cat));
    }

    @Transactional
    public void delete(Long id) {
        NomenclatureCategory cat = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Категория не найдена"));
        if (cat.isSystem() && !canManageSystemCategories()) {
            throw new BusinessException("Системную категорию может удалить только директор или владелец сети");
        }
        categoryRepository.delete(cat);
    }

    public boolean canManageSystemCategories() {
        String role = SecurityUtils.currentUser().getRole().getName();
        return RoleName.canManageSystemCategories(role);
    }

    /** @deprecated используйте {@link #canManageSystemCategories()} */
    @Deprecated
    public boolean isAdminOrTest() {
        return canManageSystemCategories();
    }

    private NomenclatureCategoryDto mapToDto(NomenclatureCategory nc) {
        NomenclatureCategoryDto ncd = new NomenclatureCategoryDto();
        ncd.setId(nc.getId());
        ncd.setCode(nc.getCode());
        ncd.setName(nc.getName());
        ncd.setSystem(nc.isSystem());
        ncd.setCreatedAt(nc.getCreatedAt());
        ncd.setParentId(nc.getParentId());
        return ncd;
    }
}
