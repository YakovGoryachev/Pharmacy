package com.example.pharmacy.Controllers;

import com.example.pharmacy.DTO.NomenclatureCategoryDto;
import com.example.pharmacy.Service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nomenclature/categories")
public class CategoryWebController {

    private final CategoryService categoryService;

    public CategoryWebController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("categoryForm", new NomenclatureCategoryDto());
        model.addAttribute("canManageSystem", categoryService.isAdminOrTest());
        model.addAttribute("pageTitle", "Категории товаров");
        model.addAttribute("activeNav", "nomenclature");
        return "nomenclature-categories";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String code,
                       @RequestParam(required = false, defaultValue = "false") boolean system,
                       RedirectAttributes ra) {
        NomenclatureCategoryDto dto = new NomenclatureCategoryDto();
        dto.setId(id);
        dto.setName(name);
        dto.setCode(code);
        categoryService.save(dto, system);
        ra.addFlashAttribute("successMessage", id != null ? "Категория обновлена" : "Категория добавлена");
        return "redirect:/nomenclature/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        categoryService.delete(id);
        ra.addFlashAttribute("successMessage", "Категория удалена");
        return "redirect:/nomenclature/categories";
    }
}
