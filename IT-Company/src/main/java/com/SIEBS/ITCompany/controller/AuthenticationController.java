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
  public ResponseEntity<LoginResponse> authenticate(
      @RequestBody AuthenticationRequest request,
      HttpServletResponse response

  ) {
    AuthenticationResponse authResponse = service.authenticate(request);

    if(authResponse != null){
      Cookie accessTokenCookie = new Cookie("access_token", authResponse.getAccessToken());
      accessTokenCookie.setHttpOnly(true);
      accessTokenCookie.setDomain("localhost");
      accessTokenCookie.setPath("/");
      response.addCookie(accessTokenCookie);

      Cookie refreshTokenCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
      refreshTokenCookie.setHttpOnly(true);
      accessTokenCookie.setDomain("localhost");
      refreshTokenCookie.setPath("/api/v1/auth/refresh-token");
      response.addCookie(refreshTokenCookie);
      return ResponseEntity.ok(authResponse.getLoginResponse());
    }
    return ResponseEntity.ok(authResponse.getLoginResponse());

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

}
