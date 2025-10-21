package com.example.medicineordering.controller;

import com.example.medicineordering.model.Medicine;
import com.example.medicineordering.model.Prescription;
import com.example.medicineordering.repository.MedicineRepository;
import com.example.medicineordering.repository.PrescriptionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PharmacistController(MedicineRepository medicineRepository, PrescriptionRepository prescriptionRepository) {
        this.medicineRepository = medicineRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard() {
        return "pharmacist_dashboard";
    }

    @GetMapping("/medicines")
    public String medicines(Model model) {
        List<Medicine> medicines = medicineRepository.findAll();
        model.addAttribute("medicines", medicines);
        model.addAttribute("medicine", new Medicine());
        return "pharmacist_medicines";
    }

    @GetMapping("/prescriptions")
    public String prescriptions(Model model) {
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        model.addAttribute("prescriptions", prescriptions);
        return "pharmacist_prescriptions";
    }

    @PostMapping("/medicine")
    public String addOrUpdateMedicine(@ModelAttribute("medicine") Medicine medicine, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/pharmacist/medicines";
        }
        // if exists by name, update stock/price/manufacturer
        medicineRepository.findByName(medicine.getName())
                .ifPresent(existing -> {
                    medicine.setId(existing.getId());
                });
        medicineRepository.save(medicine);
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/medicine/{id}/delete")
    public String deleteMedicine(@PathVariable Integer id) {
        medicineRepository.deleteById(id);
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/medicine/{id}/stock")
    public String updateMedicineStock(@PathVariable Integer id, @RequestParam("stockQuantity") Integer stockQuantity) {
        medicineRepository.findById(id).ifPresent(m -> {
            m.setStockQuantity(stockQuantity);
            medicineRepository.save(m);
        });
        return "redirect:/pharmacist/medicines";
    }

    @PostMapping("/prescription/{id}/approve")
    public String approvePrescription(@PathVariable Integer id) {
        prescriptionRepository.findById(id).ifPresent(p -> {
            p.setStatus("APPROVED");
            prescriptionRepository.save(p);
        });
        return "redirect:/pharmacist/prescriptions";
    }

    @PostMapping("/prescription/{id}/reject")
    public String rejectPrescription(@PathVariable Integer id) {
        prescriptionRepository.findById(id).ifPresent(p -> {
            p.setStatus("REJECTED");
            prescriptionRepository.save(p);
        });
        return "redirect:/pharmacist/dashboard";
    }
}


