package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.ProductType;
import com.example.pharmacy.Service.CategoryService;
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
import java.util.Map;

@Controller
@RequestMapping("/nomenclature")
public class NomenclatureController {
    private final NomenclatureService nomenclatureService;
    private final CategoryService categoryService;

    @Autowired
    public NomenclatureController(NomenclatureService nomenclatureService,
                                  CategoryService categoryService){
        this.nomenclatureService = nomenclatureService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listNomen(@RequestParam(required = false) String q,
            @RequestParam(required = false) String atx,
            @RequestParam(required = false) Long category, //catId
            @RequestParam(required = false) String flags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "brandName") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            Model model){

        Page<NomenclatureDto> nlDto = nomenclatureService.findFilteredNomenclature(q, atx, category, flags, page, size, sort, dir);

        model.addAttribute("nomenclatures", nlDto.getContent());
        model.addAttribute("currentPage", nlDto.getNumber());
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", nlDto.getTotalPages());

        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Справочник номенклатуры");
        model.addAttribute("activeNav", "nomenclature");
        model.addAttribute("productTypes", ProductType.values());

        return "nomenclature-list";
    }


    @GetMapping("/create")
    public String addNomenclature(Model model){
        NomenclatureDto dto = new NomenclatureDto();
        dto.setProductType(ProductType.MEDICINE.name());
        model.addAttribute("nomCommand", dto);
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("pageTitle", "Новая номенклатура");
        model.addAttribute("activeNav", "nomenclature");
        model.addAttribute("productTypes", ProductType.values());
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
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("pageTitle", "Редактирование номенклатуры");
        model.addAttribute("activeNav", "nomenclature");
        model.addAttribute("productTypes", ProductType.values());
        return "nomenclature-edit";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        nomenclatureService.delete(id);
        return "redirect:/nomenclature";
    }
}
