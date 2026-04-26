package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/batches")
public class WarehouseController {

    private final BatchService batchService;

    @Autowired
    public WarehouseController(BatchService batchService){
        this.batchService = batchService;
    }

    @GetMapping("")
    public String main(@RequestParam(required = false) String numBatch,
                        @RequestParam(required = false) String supplier,
                        @RequestParam(required = false) LocalDate dateEntrance,
                        @RequestParam(required = false) LocalDate expiryDate,
                        @RequestParam(defaultValue = "receivedDate") String sort,
                        @RequestParam(defaultValue = "desc") String dir,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size, Model model){

        Page<Batch> bl = batchService.findFilteredBatches(numBatch, supplier, dateEntrance, expiryDate, page, size);

        model.addAttribute("batches", bl);
        model.addAttribute("currentPage", bl.getNumber());
        model.addAttribute("totalPages", bl.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);

//        model.addAttribute("param", java.util.Map.of(
//                "numBatch", numBatch,
//                "supplier", supplier,
//                "dateEntrance", dateEntrance,
//                "expiryDate", expiryDate)); exception

        return "warehouse-main";
    }

    @GetMapping("/create")
    public String priemka(Model model){
        model.addAttribute("batchCommand", new BatchDto());
        return "warehouse-acceptance";
    }
    @PostMapping("/create")
    public String create(@ModelAttribute BatchDto bdto, Model model){

        if (bdto.getNomenclatureId() == null || bdto.getNomenclatureId() < 0){
            model.addAttribute("batchCommand", bdto);
            model.addAttribute("nomenclatureError", "Выберите препарат из справочника");
            return "warehouse-acceptance";
        }


        batchService.save(bdto);
        return "redirect:/batches/create";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        BatchDto bdto = batchService.findById(id);
        if (bdto == null) return "redirect:/batch?error=notFound";



        model.addAttribute("batchCommand", bdto);
        return "warehouse-edit";
    }

    @PostMapping("/delete/{id}")
    public String deleteBatch(@PathVariable Long id) {
        batchService.deleteById(id);
        return "redirect:/batches";
    }
}
