package com.example.pharmacy.ControllersRest;

import com.example.pharmacy.DTO.AtcManualDto;
import com.example.pharmacy.Service.AtcManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/atx")
public class AtxController {

    @Autowired
    private AtcManualService atxService;

    @GetMapping("/search")
    public ResponseEntity<List<AtcManualDto>> searchAtx(@RequestParam String query) {
        if (query == null || query.length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<AtcManualDto> results = atxService.searchByCodeOrName(query);
        return ResponseEntity.ok(results);
    }
}
