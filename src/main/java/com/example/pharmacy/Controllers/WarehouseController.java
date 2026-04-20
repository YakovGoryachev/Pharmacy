package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WarehouseController {

    private final BatchService batchService;

    @Autowired
    public WarehouseController(BatchService batchService){
        this.batchService = batchService;
    }

    @GetMapping("/ware")
    public String main(@RequestParam(required = false) String numBatch,
                        @RequestParam(required = false) String supplier,
                        @RequestParam(required = false) LocalDate dateEntrance,
                        @RequestParam(required = false) LocalDate expiryDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size, Model model){

        Page<Batch> bl = batchService.findFilteredBatches(numBatch, supplier, dateEntrance, expiryDate, page, size);

        model.addAttribute("batches", bl);

        return "warehouse-main";
    }

    @GetMapping("batches/reception")
    public String priemka(Model model){
        model.addAttribute("batchCommand", new BatchDto());
        return "warehouse-acceptance";
    }
}
