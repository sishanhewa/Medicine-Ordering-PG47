package com.example.medicineordering.repository;

import com.example.medicineordering.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByCustomerUsernameOrderByUploadedAtDesc(String customerUsername);
}


