package com.example.pharmacy.Service;

import com.example.pharmacy.Pojo.Address;
import com.example.pharmacy.Pojo.Pharmacy;
import com.example.pharmacy.Repository.PharmacyRepository;
import com.example.pharmacy.Repository.UserRepository;
import com.example.pharmacy.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final UserRepository userRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository, UserRepository userRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.userRepository = userRepository;
    }

    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }

    public Pharmacy findById(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Аптека не найдена"));
    }

    @Transactional
    public Pharmacy save(Pharmacy pharmacy) {
        if (pharmacy.getName() == null || pharmacy.getName().isBlank()) {
            throw new BusinessException("Укажите название аптеки");
        }
        if (pharmacy.getAddress() == null) {
            pharmacy.setAddress(new Address());
        }
        return pharmacyRepository.save(pharmacy);
    }

    @Transactional
    public void delete(Long id) {
        Pharmacy pharmacy = findById(id);
        if (userRepository.countByPharmacyId(id) > 0) {
            pharmacy.setActive(false);
            pharmacyRepository.save(pharmacy);
        } else {
            pharmacyRepository.delete(pharmacy);
        }
    }
}
