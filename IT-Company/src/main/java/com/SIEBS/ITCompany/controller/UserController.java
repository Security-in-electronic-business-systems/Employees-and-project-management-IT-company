package com.SIEBS.ITCompany.controller;


import com.SIEBS.ITCompany.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final EmailService emailService;

    @GetMapping("/testEmail")
    public void TestEmail(){
        emailService.sendEmail("tasaantic00@gmail.com", "TEST", "Slanje emaila radi! :)");
    }


}
