package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.NomenclatureDto;
import com.example.pharmacy.Pojo.AtcManual;
import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Pojo.NomenclatureCategory;
import com.example.pharmacy.Repository.AtcManualRepository;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.CategoryRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import com.example.pharmacy.Specifications.BatchSpecifications;
import com.example.pharmacy.Specifications.NomenclatureSpecifications;
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

//todo доделать работу с листом productCategory

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

    public Page<Nomenclature> findFilteredNomenclature(String search, String atx, Long categoryId, String filter, int page, int size){
        Specification<Nomenclature> spec = NomenclatureSpecifications.hasFilters(
                search, atx, categoryId, filter
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("brandName").descending());

        return nomenclatureRepository.findAll(spec, pageable);
    }
    public void save(NomenclatureDto nmd){
        nomenclatureRepository.save(toPojo(nmd));
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

    public NomenclatureDto toDto(Nomenclature nm){
        NomenclatureDto nmd = new NomenclatureDto();

        nmd.setId(nm.getId());
        nmd.setAtxId(nm.getAtcManual().getId());
        nmd.setMnn(nm.getMnn());
        nmd.setAtxCode(nm.getAtcManual().getCode());
        nmd.setBrandName(nm.getBrandName());
        nmd.setFormOfRelease(nm.getFormOfRelease());
        nmd.setDosage(nm.getDosage());
        nmd.setDosageUnit(nm.getDosageUnit());
        nmd.setQtyInPack(nm.getQuantityInPack());
        nmd.setManufacturer(nm.getManufacturer());
        nmd.setCountry(nm.getCountry());
        nmd.setBarcode(nm.getBarcode());
        nmd.setPrice(nm.getPrice());
        nmd.setMinStockLevel(nm.getMinStockLevel());
        nmd.setReceipt(nm.getReceipt());
        nmd.setNarcotic(nm.getNarcotic());
        nmd.setPsycho(nm.getPsychotropic());

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

        if (nmd.getAtxId() != null) {
            am = atcManualRepository.findById(nmd.getAtxId())
                    .orElseThrow();
        }else{
            am = atcManualRepository.findByCode(nmd.getAtxCode());
        }

        nm.setId(nmd.getId());
        nm.setMnn(nmd.getMnn());
        nm.setDosageUnit(nmd.getDosageUnit());
        nm.setDosage(nmd.getDosage());
        nm.setBarcode(nmd.getBarcode());
        nm.setBrandName(nmd.getBrandName());
        nm.setCountry(nmd.getCountry());
        nm.setAtcManual(am);
        nm.setFormOfRelease(nmd.getFormOfRelease());
        nm.setManufacturer(nmd.getManufacturer());
        nm.setMinStockLevel(nmd.getMinStockLevel());
        nm.setBarcode(nmd.getBarcode());
        nm.setNarcotic(nmd.getNarcotic());
        nm.setPsychotropic(nmd.getPsycho());
        nm.setReceipt(nmd.getReceipt());
        nm.setPrice(nmd.getPrice());
        nm.setQuantityInPack(nmd.getQtyInPack());
        return nm;
    }
}
