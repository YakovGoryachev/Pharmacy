package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.BatchDto;
import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Pojo.Nomenclature;
import com.example.pharmacy.Repository.BatchRepository;
import com.example.pharmacy.Repository.NomenclatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.example.pharmacy.Specifications.BatchSpecifications;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final NomenclatureRepository nomenclatureRepository;

    @Autowired
    public BatchService(BatchRepository batchRepository,
                        NomenclatureRepository nomenclatureRepository){
        this.batchRepository = batchRepository;
        this.nomenclatureRepository = nomenclatureRepository;
    }

    public Page<Batch> findFilteredBatches(String numBatches, String supplier, LocalDate dateEntrance, LocalDate expiryDate, int page, int size){
        Specification<Batch> spec = BatchSpecifications.hasFilters(
                numBatches, supplier, dateEntrance, expiryDate
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("receivedDate").descending());

        return batchRepository.findAll(spec, pageable);
    }
    public void save(BatchDto bdto){
        batchRepository.save(mapToPojo(bdto));
    }
    public BatchDto findById(Long id){
        Batch b = batchRepository.findById(id)
                .orElseThrow();
        BatchDto bdto = mapToDto(b);
        return bdto;
    }

    public void deleteById(Long id){
        batchRepository.deleteById(id);
    }

    private Batch mapToPojo(BatchDto bdto){
        Batch b = new Batch();
        Nomenclature n = nomenclatureRepository.findById(bdto.getNomenclatureId())
                        .orElseThrow();
        b.setId(bdto.getId());
        b.setNomenclature(n);
        b.setBatchNumber(bdto.getBatchNumber());
        b.setExpiryDate(bdto.getExpiryDate());
        b.setProductionDate(bdto.getProductionDate());
        b.setReceivedDate(bdto.getReceivedDate());
        b.setSupplier(bdto.getSupplier());
        b.setPrice(bdto.getPrice());
        b.setQtyReceived(bdto.getQtyInStock());
        b.setQtyInStock(bdto.getQtyInStock());
        b.setStorageZone(bdto.getStorageZone());
        b.setWrittenOff(bdto.getWrittenOff());
        return b;
    }

    private BatchDto mapToDto(Batch b){
        BatchDto bdto = new BatchDto();
        bdto.setId(b.getId());
        bdto.setNomenclatureId(b.getNomenclature().getId());
        bdto.setBatchNumber(b.getBatchNumber());
        bdto.setExpiryDate(b.getExpiryDate());
        bdto.setProductionDate(b.getProductionDate());
        bdto.setReceivedDate(b.getReceivedDate());
        bdto.setSupplier(b.getSupplier());
        bdto.setPrice(b.getPrice());
        bdto.setQtyReceived(b.getQtyInStock());
        bdto.setQtyInStock(b.getQtyInStock());
        bdto.setStorageZone(b.getStorageZone());
        bdto.setWrittenOff(b.getWrittenOff());

        return bdto;
    }
}
