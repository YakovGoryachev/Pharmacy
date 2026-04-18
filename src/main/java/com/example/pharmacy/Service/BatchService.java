package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.Batch;
import com.example.pharmacy.Repository.BatchRepository;
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

    @Autowired
    public BatchService(BatchRepository batchRepository){
        this.batchRepository = batchRepository;
    }

    public Page<Batch> findFilteredBatches(String numBatches, String supplier, LocalDate dateEntrance, LocalDate expiryDate, int page, int size){
        Specification<Batch> spec = BatchSpecifications.hasFilters(
                numBatches, supplier, dateEntrance, expiryDate
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("receivedDate").descending());

        return batchRepository.findAll(spec, pageable);
    }
}
