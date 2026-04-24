package com.example.pharmacy.ControllersRest;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service){
        this.service = service;
    }

    @GetMapping("/search")
    public ResponseEntity<List<NomenclatureCategoryDto>> search(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) return ResponseEntity.ok(Collections.emptyList());
        return ResponseEntity.ok(service.search(query));
    }

    @PostMapping("/create")
    public ResponseEntity<NomenclatureCategoryDto> create(@RequestBody NomenclatureCategoryDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.getOrCreate(dto.getName()));
    }
}
