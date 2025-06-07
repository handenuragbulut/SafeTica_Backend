package com.safetica.safetica_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        // ✅ Orijinal dosya adı
        String originalFilename = file.getOriginalFilename();

        // ✅ Uzantıyı ayır
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // ✅ Benzersiz isim oluştur
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // ✅ Klasörü oluştur (varsa sorun olmaz)
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ✅ Dosya yolu
        Path filePath = uploadPath.resolve(uniqueFileName);

        // ✅ Dosyayı yaz
        file.transferTo(filePath);

        // ✅ URL/path bilgisini döndür (frontend’e bu verilecek)
        return "/" + UPLOAD_DIR + uniqueFileName;
    }
}
