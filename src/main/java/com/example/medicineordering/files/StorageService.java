package com.example.medicineordering.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class StorageService {
    private final Path root;

    public StorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String save(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = prefix + "_" + UUID.randomUUID() + ext;
        Path dest = this.root.resolve(filename);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + filename;
    }

    public String saveBytes(byte[] bytes, String prefix, String ext) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        String filename = prefix + "_" + UUID.randomUUID() + "." + ext;
        Path dest = this.root.resolve(filename);
        Files.createDirectories(dest.getParent());
        try (OutputStream os = Files.newOutputStream(dest)) {
            os.write(bytes);
        }
        return "/uploads/" + filename;
    }
}