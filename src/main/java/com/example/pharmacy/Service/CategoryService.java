package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.Pojo.NomenclatureCategory;
import com.example.pharmacy.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<NomenclatureCategoryDto> search(String query){
        return categoryRepository.findByNameContainingIgnoreCase(query)
                .stream().limit(10).map(this::mapToDto).collect(Collectors.toList());
    }

    public NomenclatureCategoryDto getOrCreate(String name) {
        return categoryRepository.findByNameIgnoreCase(name.trim())
                .map(this::mapToDto)
                .orElseGet(() -> {
                    NomenclatureCategory cat = new NomenclatureCategory();
                    cat.setName(name.trim());
                    return mapToDto(categoryRepository.save(cat));
                });
    }

    public List<NomenclatureCategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private NomenclatureCategoryDto mapToDto(NomenclatureCategory nc){
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
