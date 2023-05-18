package com.SIEBS.ITCompany.controller;


import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.service.EmailService;
import com.SIEBS.ITCompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final UserService userService;

    @GetMapping("/testEmail")
    public void TestEmail(){
        emailService.sendEmail("tasaantic00@gmail.com", "TEST", "Slanje emaila radi! :)");
    }

    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users;
    }

}
