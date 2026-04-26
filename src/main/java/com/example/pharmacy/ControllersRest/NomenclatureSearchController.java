package com.example.pharmacy.ControllersRest;

import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Service.NomenclatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/nomenclature")
public class NomenclatureSearchController {
    private final NomenclatureService nomenclatureService;

    public NomenclatureSearchController(NomenclatureService nomenclatureService){
        this.nomenclatureService = nomenclatureService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<NomenclatureDto>> searchNomenclature(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<NomenclatureDto> results = nomenclatureService.searchByNameOrMnn(query.trim(), 10);
        return ResponseEntity.ok(results);
    }
}
