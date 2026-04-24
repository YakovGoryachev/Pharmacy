package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Service.NomenclatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/nomenclature")
public class NomenclatureController {
    private final NomenclatureService nomenclatureService;

    @Autowired
    public NomenclatureController(NomenclatureService nomenclatureService){
        this.nomenclatureService = nomenclatureService;
    }

    @GetMapping("")
    public String listNomen(@RequestParam(required = false) String q,
            @RequestParam(required = false) String atx,
            @RequestParam(required = false) Long catId,
            @RequestParam(required = false) String flags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model){

        List<NomenclatureDto> ndl = new ArrayList<>();
        Page<Nomenclature> nl = nomenclatureService.findFilteredNomenclature(q, atx, catId, flags, page, size);

        model.addAttribute("nomenclatures", nl);

        return "nomenclature-list";
    }


    @GetMapping("/create")
    public String addNomenclature(Model model){
        model.addAttribute("nomCommand", new NomenclatureDto());
        model.addAttribute("categories", new NomenclatureCategoryDto());
        return "nomenclature-create";
    }

    @PostMapping("/save/close")
    public String saveAndClose(@ModelAttribute NomenclatureDto ndto){
        nomenclatureService.save(ndto);
        return "redirect:/nomenclature";
    }

    @PostMapping("/save/continue")
    public String saveAndContinue(@ModelAttribute NomenclatureDto ndto){
        nomenclatureService.save(ndto);
        return "redirect:/nomenclature/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        NomenclatureDto ndto = nomenclatureService.findById(id);
        model.addAttribute("nomCommand", ndto);
        model.addAttribute("categories", new NomenclatureCategoryDto());
        return "nomenclature-edit";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        nomenclatureService.delete(id);
        return "redirect:/nomenclature";
    }
}
