package com.dhi.findme_backend.service;

public interface EmailService {
    
    void sendEmail(String to, String subject, String body);
}
