package com.safetica.safetica_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("SafeTica - Email Doğrulama Kodu");
        message.setText("SafeTica hesabinizi aktif etmek için doğrulama kodunuz: " + verificationCode);

        mailSender.send(message);
    }
}
