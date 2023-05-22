package com.SIEBS.ITCompany.controller;


import com.SIEBS.ITCompany.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private EmailService emailService;
    public UserController(EmailService emailService){
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendMail(@RequestParam(value = "file", required = false)MultipartFile[] file, String to, String subject, String body){
        return emailService.sendMail(file, to, subject, body);
    }


}
