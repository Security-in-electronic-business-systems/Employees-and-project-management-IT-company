package com.SIEBS.ITCompany.controller;
import com.SIEBS.ITCompany.dto.AuthenticationRequest;
import com.SIEBS.ITCompany.dto.AuthenticationResponse;
import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.dto.RegisterRequest;
import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

 // @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response

  ) {
    AuthenticationResponse authResponse = service.authenticate(request);

    Cookie accessTokenCookie = new Cookie("access_token", authResponse.getAccessToken());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    response.addCookie(refreshTokenCookie);
    return ResponseEntity.ok(authResponse);
  }

 // @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @GetMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> passwordlessAuthenticate(
          @RequestParam("token") String token,
          HttpServletRequest request,
          HttpServletResponse response
  ) {
    AuthenticationResponse authResponse = service.generateAccessAndRefresToken(token);
    URI redirectUri = URI.create("http://localhost:5173/home");

    Cookie accessTokenCookie = new Cookie("access_token", authResponse.getAccessToken());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(redirectUri)
            .body(authResponse);
  }

 // @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @PostMapping("/passwordless-authenticate")
  public ResponseEntity<MessageResponse> generateAndSendToken(
          @RequestBody PasswordlessAuthenticationRequest request
  ) {
    return ResponseEntity.ok(MessageResponse.builder().message(service.generateAndSendToken(request)).build());
  }

  //@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(
          @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }

 // @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


  @GetMapping("/message")
  public ResponseEntity<String> getMessage() {
    String message = "Ovo je poruka sa servera.";
    return ResponseEntity.ok(message);
  }
 // @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
  @GetMapping("/endpoint")
  public ResponseEntity<MessageResponse> passwordlessAuthenticate(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    String accessToken = "";
    String refreshToken = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("access_token")) {
          accessToken = cookie.getValue();
          // Ovdje možete obraditi pronađeni access token
          System.out.println("Access Token: " + accessToken);
        } else if (cookie.getName().equals("refresh_token")) {
          refreshToken = cookie.getValue();
          // Ovdje možete obraditi pronađeni refresh token
          System.out.println("Refresh Token: " + refreshToken);
        }
      }
    }

    return ResponseEntity.ok(new MessageResponse("Access token: " + accessToken + " , Refresh token: " + refreshToken));
  }
}
