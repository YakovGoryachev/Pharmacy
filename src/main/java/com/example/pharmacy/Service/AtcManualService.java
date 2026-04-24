package com.example.pharmacy.Service;

import com.example.pharmacy.DTO.AtcManualDto;
import com.example.pharmacy.Pojo.AtcManual;
import com.example.pharmacy.Repository.AtcManualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtcManualService {
    private final AtcManualRepository atcManualRepository;

    @Autowired
    public AtcManualService(AtcManualRepository atcManualRepository) {
        this.atcManualRepository = atcManualRepository;
    }

    public List<AtcManualDto> searchByCodeOrName(String query){
        return atcManualRepository.searchByCodeOrName(query.toUpperCase())
                .stream().limit(10).map(this::mapToDto).collect(Collectors.toList());
    }

    private AtcManualDto mapToDto(AtcManual atc){
        AtcManualDto atcDto = new AtcManualDto();
        atcDto.setId(atc.getId());
        atcDto.setCode(atc.getCode());
        atcDto.setName(atc.getName());
        atcDto.setLevel(atc.getLevel());
        atcDto.setParentId(atc.getParentId());
        atcDto.setCreatedAt(atc.getCreatedAt());
        return atcDto;
    }
}
