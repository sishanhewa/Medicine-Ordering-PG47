package com.example.medicineordering.controller;

import com.example.medicineordering.model.Prescription;
import com.example.medicineordering.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final PrescriptionRepository prescriptionRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public CustomerController(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @GetMapping("")
    public String landingPage() {
        return "customer_landing";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "customer_upload";
    }

    @GetMapping("/prescription")
    public String prescriptionLegacyRedirect() {
        return "redirect:/customer/prescriptions";
    }

    @GetMapping("/prescriptions")
    public String prescriptionsPage(Authentication auth, Model model) {
        String username = auth.getName();
        List<Prescription> list = prescriptionRepository.findByCustomerUsernameOrderByUploadedAtDesc(username);
        model.addAttribute("prescriptions", list);
        return "customer_prescriptions";
    }

    @PostMapping("/prescription/upload")
    public String upload(Authentication auth, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "redirect:/customer/prescription";
        }
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path dest = dir.resolve(System.currentTimeMillis() + "_" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), dest);

        Prescription p = new Prescription();
        p.setCustomerUsername(auth.getName());
        p.setFileName(file.getOriginalFilename());
        p.setFilePath(dest.toString());
        p.setStatus("PENDING");
        prescriptionRepository.save(p);
        return "redirect:/customer/prescription";
    }
    @GetMapping("/prescription/file/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Integer id) throws IOException {
        return prescriptionRepository.findById(id)
                .map(p -> {
                    Resource resource = new FileSystemResource(p.getFilePath());
                    String filename = p.getFileName();
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}


