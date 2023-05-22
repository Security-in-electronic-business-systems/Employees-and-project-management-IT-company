package com.SIEBS.ITCompany.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface EmailService {
    public String sendMail(MultipartFile[] file, String to, String subject, String body);
}