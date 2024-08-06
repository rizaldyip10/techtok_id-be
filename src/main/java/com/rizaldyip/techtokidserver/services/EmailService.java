package com.rizaldyip.techtokidserver.services;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendEmail(String to, String token, String htmlPath) throws MessagingException, IOException;
    void sendEmail(String to, String htmlPath) throws MessagingException, IOException;
}
