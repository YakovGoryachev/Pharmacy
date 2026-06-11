package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.AtcManual;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Pojo.NomenclatureCategory;
import com.example.pharmacy.Pojo.ProductType;
import com.example.pharmacy.Repository.AtcManualRepository;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.CategoryRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Specifications.BatchSpecifications;
import com.example.pharmacy.Specifications.NomenclatureSpecifications;
import com.example.pharmacy.audit.Audited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NomenclatureService {
    private final NomenclatureRepository nomenclatureRepository;
    private final CategoryRepository categoryRepository;
    private final AtcManualRepository atcManualRepository;

    @Autowired
    public NomenclatureService(NomenclatureRepository nomenclatureRepository,
                               CategoryRepository categoryRepository,
                               AtcManualRepository atcManualRepository){
        this.nomenclatureRepository = nomenclatureRepository;
        this.categoryRepository = categoryRepository;
        this.atcManualRepository = atcManualRepository;
    }

    public Page<NomenclatureDto> findFilteredNomenclature(String search, String atx, Long categoryId, String filter,
                                                         int page, int size, String sort, String dir) {
        Specification<Nomenclature> spec = NomenclatureSpecifications.hasFilters(
                search, atx, categoryId, filter
        );
        Sort s = "asc".equalsIgnoreCase(dir) ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, s);

        Page<Nomenclature> entityPage = nomenclatureRepository.findAll(spec, pageable);
        return entityPage.map(this::toDto);
    }
    @Audited(entity = "Nomenclature", action = "SAVE")
    public Nomenclature save(NomenclatureDto nmd){
        Nomenclature nm = toPojo(nmd);
        if (nmd.getId() != null) {
            nomenclatureRepository.findById(nmd.getId())
                    .ifPresent(existing -> nm.setPrice(existing.getPrice()));
        } else {
            nm.setPrice(null);
        }
        return nomenclatureRepository.save(nm);
    }
    public NomenclatureDto findById(Long id){
        Nomenclature nm = nomenclatureRepository.findById(id)
                .orElseThrow();
        NomenclatureDto nmd = toDto(nm);

        return nmd;
    }
    public void delete(Long id){
        nomenclatureRepository.deleteById(id);
    }

    public List<NomenclatureDto> searchByNameOrMnn(String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return nomenclatureRepository.searchByQuery(query.trim(), PageRequest.of(0, limit))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public NomenclatureDto toDto(Nomenclature nm){
        NomenclatureDto nmd = new NomenclatureDto();

        nmd.setId(nm.getId());
        nmd.setProductType(nm.getProductType());
        if (nm.getNomenclatureCategories() != null && !nm.getNomenclatureCategories().isEmpty()) {
            NomenclatureCategory cat = nm.getNomenclatureCategories().getFirst();
            nmd.setProductCategoryId(cat.getId());
            nmd.setProductCategoryName(cat.getName());
        }
        if (nm.getAtcManual() != null) {
            nmd.setAtxId(nm.getAtcManual().getId());
            nmd.setAtxCode(nm.getAtcManual().getCode());
        }
        nmd.setMnn(nm.getMnn());
        nmd.setBrandName(nm.getBrandName());
        nmd.setFormOfRelease(nm.getFormOfRelease());
        nmd.setDosage(nm.getDosage());
        nmd.setDosageUnit(nm.getDosageUnit());
        nmd.setQtyInPack(nm.getQuantityInPack());
        nmd.setManufacturer(nm.getManufacturer());
        nmd.setCountry(nm.getCountry());
        nmd.setBarcode(nm.getBarcode());
        nmd.setMinStockLevel(nm.getMinStockLevel());
        nmd.setReceipt(nm.getReceipt());
        nmd.setNarcotic(nm.getNarcotic());
        nmd.setPsychotropic(nm.getPsychotropic());

        nmd.setDisplayText(buildDisplayText(nm));
        nmd.setMarked(nm.getMarked());

        return nmd;
    }

    public Nomenclature toPojo(NomenclatureDto nmd){
        Nomenclature nm = new Nomenclature();
        AtcManual am;

        if (nmd.getProductCategoryId() != null) {
            NomenclatureCategory nc = categoryRepository.findById(nmd.getProductCategoryId())
                    .orElseThrow();
            List<NomenclatureCategory> ncList = new ArrayList<>();
            ncList.add(nc);
            nm.setNomenclatureCategories(ncList);
        }

        String type = nmd.getProductType();
        nm.setProductType(type == null || type.isBlank() ? ProductType.MEDICINE.name() : type);
        nm.setId(nmd.getId());

        am = null;
        if (ProductType.MEDICINE.name().equals(nm.getProductType())) {
            if (nmd.getAtxId() != null) {
                am = atcManualRepository.findById(nmd.getAtxId()).orElse(null);
            } else if (nmd.getAtxCode() != null && !nmd.getAtxCode().isBlank()) {
                am = atcManualRepository.findByCode(nmd.getAtxCode());
            }
            nm.setMnn(nmd.getMnn());
            nm.setReceipt(nmd.getReceipt());
            nm.setNarcotic(nmd.getNarcotic());
            nm.setPsychotropic(nmd.getPsychotropic());
        } else {
            nm.setMnn(nmd.getMnn() != null && !nmd.getMnn().isBlank() ? nmd.getMnn() : null);
            nm.setReceipt(false);
            nm.setNarcotic(false);
            nm.setPsychotropic(false);
        }
        nm.setDosageUnit(nmd.getDosageUnit());
        nm.setDosage(nmd.getDosage());
        nm.setBarcode(nmd.getBarcode());
        nm.setBrandName(nmd.getBrandName());
        nm.setCountry(nmd.getCountry());
        if (am != null) {
            nm.setAtcManual(am);
        }
        nm.setMarked(nmd.getMarked() != null ? nmd.getMarked() : false);
        nm.setFormOfRelease(nmd.getFormOfRelease());
        nm.setManufacturer(nmd.getManufacturer());
        nm.setMinStockLevel(nmd.getMinStockLevel());
        nm.setBarcode(nmd.getBarcode());
        nm.setQuantityInPack(nmd.getQtyInPack());
        return nm;
    }

    private String buildDisplayText(Nomenclature nm) {
        if (!ProductType.MEDICINE.name().equals(nm.getProductType())) {
            String text = nm.getBrandName();
            if (nm.getQuantityInPack() != null) {
                text += ", " + nm.getQuantityInPack() + " шт.";
            }
            return text;
        }
        return nm.getBrandName()
                + (nm.getDosage() != null ? " " + nm.getDosage() : "")
                + (nm.getDosageUnit() != null ? " " + nm.getDosageUnit() : "");
    }
}
