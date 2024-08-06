package com.rizaldyip.techtokidserver.services.impl;

import com.rizaldyip.techtokidserver.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String token, String htmlPath) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("techtokidecommerce@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, to);

        String htmlTemplate = readFile(htmlPath);
        String htmlContent = htmlTemplate.replace("${feURL}", "http://localhost:3000");
        htmlContent = htmlContent.replace("${token}", token);

        message.setContent(htmlContent, "text/html; charset=utf-8");

        this.mailSender.send(message);
    }

    @Override
    public void sendEmail(String to, String htmlPath) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("techtokidecommerce@gmail.com");
        message.setRecipients(MimeMessage.RecipientType.TO, to);

        String htmlTemplate = readFile(htmlPath);
        String htmlContent = htmlTemplate.replace("${feURL}", "http://localhost:3000");

        message.setContent(htmlContent, "text/html; charset=utf-8");

        this.mailSender.send(message);
    }

    public String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
