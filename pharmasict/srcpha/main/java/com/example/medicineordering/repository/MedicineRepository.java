package com.example.medicineordering.repository;

import com.example.medicineordering.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    Optional<Medicine> findByName(String name);
}


