package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.ContactMessage;
import com.safetica.safetica_backend.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactMessageRepository messageRepo;
    private final JavaMailSender mailSender;

    @Autowired
    public ContactController(ContactMessageRepository messageRepo, JavaMailSender mailSender) {
        this.messageRepo = messageRepo;
        this.mailSender = mailSender;
    }

    // Kullanıcı mesaj gönderir
    @PostMapping
    public ResponseEntity<?> sendContactMessage(@RequestBody ContactMessage request) {
        request.setStatus("NEW");
        request.setCreatedAt(LocalDateTime.now());
        messageRepo.save(request);

        // Admin'e e-posta bildirimi
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("safetica2025@gmail.com");
        mailMessage.setSubject("Yeni İletişim Mesajı Var!");
        mailMessage.setText(
                "Ad: " + request.getName() + "\n" +
                        "E-posta: " + request.getEmail() + "\n" +
                        "Mesaj:\n" + request.getMessage());
        mailSender.send(mailMessage);

        return ResponseEntity.ok("Mesajınız başarıyla gönderildi!");
    }

    // Admin panelinde mesajları listele
    @GetMapping("/messages")
    public List<ContactMessage> getAllMessages() {
        return messageRepo.findAll();
    }

    // Admin yanıt verir
    @PutMapping("/respond/{id}")
    public ResponseEntity<?> respondToMessage(@PathVariable Long id, @RequestBody String response) {
        ContactMessage message = messageRepo.findById(id).orElseThrow(() -> new RuntimeException("Mesaj bulunamadı!"));
        message.setResponse(response);
        message.setStatus("RESPONDED");
        message.setRespondedAt(LocalDateTime.now());
        messageRepo.save(message);

        // Kullanıcıya e-posta gönder (response'u ileterek)
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(message.getEmail());
        mailMessage.setSubject("İletişim Mesajınıza Yanıt Geldi");
        mailMessage.setText(
                "Merhaba " + message.getName() + ",\n\n" +
                        "Mesajınıza yanıt verdik:\n" +
                        response + "\n\n" +
                        "Teşekkür ederiz!");
        mailSender.send(mailMessage);

        return ResponseEntity.ok("Yanıt başarıyla gönderildi!");
    }
}
