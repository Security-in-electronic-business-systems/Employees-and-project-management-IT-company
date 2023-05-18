package com.SIEBS.ITCompany.controller;

import com.SIEBS.ITCompany.dto.AuthenticationResponse;
import com.SIEBS.ITCompany.dto.MessageResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
public class DemoController {

    @GetMapping("/endpoint")
    public ResponseEntity<MessageResponse> passwordlessAuthenticate(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    String accessToken = cookie.getValue();
                    // Ovdje možete obraditi pronađeni access token
                    System.out.println("Access Token: " + accessToken);
                } else if (cookie.getName().equals("refresh_token")) {
                    String refreshToken = cookie.getValue();
                    // Ovdje možete obraditi pronađeni refresh token
                    System.out.println("Refresh Token: " + refreshToken);
                }
            }
        }

        // Vaša logika i generisanje odgovora

        return ResponseEntity.ok(new MessageResponse("Uspješno izvršeno"));
    }
}
