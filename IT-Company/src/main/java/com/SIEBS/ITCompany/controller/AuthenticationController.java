package com.SIEBS.ITCompany.controller;
import com.SIEBS.ITCompany.dto.AuthenticationRequest;
import com.SIEBS.ITCompany.dto.AuthenticationResponse;
import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.dto.RegisterRequest;
import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.service.AuthenticationService;
import com.SIEBS.ITCompany.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final JwtService jwtService;

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
      refreshTokenCookie.setPath("/");
      response.addCookie(refreshTokenCookie);
      return ResponseEntity.ok(authResponse.getLoginResponse());
    }
    return ResponseEntity.ok(authResponse.getLoginResponse());

  }

  @GetMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> passwordlessAuthenticate(
          @RequestParam("token") String token,
          HttpServletRequest request,
          HttpServletResponse response
  ) {
    AuthenticationResponse authResponse = service.generateAccessAndRefresToken(token);
    URI redirectUri = URI.create("http://localhost:3000/wait-room");

      Cookie accessTokenCookie = new Cookie("access_token", authResponse.getAccessToken());
      accessTokenCookie.setHttpOnly(true);
      accessTokenCookie.setDomain("localhost");
      accessTokenCookie.setPath("/");
      response.addCookie(accessTokenCookie);

      Cookie refreshTokenCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
      refreshTokenCookie.setHttpOnly(true);
      accessTokenCookie.setDomain("localhost");
      refreshTokenCookie.setPath("/");
      response.addCookie(refreshTokenCookie);

    return ResponseEntity.status(HttpStatus.FOUND)
            .location(redirectUri)
            .body(authResponse);
  }

  @PostMapping("/generateAndSendToken")
  public ResponseEntity<MessageResponse> generateAndSendToken(
          @RequestBody PasswordlessAuthenticationRequest request
  ) {
    return ResponseEntity.ok(MessageResponse.builder().message(service.generateAndSendToken(request)).build());
  }

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(
          @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/getLoginResponse")
  public ResponseEntity<LoginResponse> getLoginResponse(
          HttpServletRequest request,
          HttpServletResponse response
  ){
    TokensDTO tokens = service.getTokensFromRequest(request);
    String email = jwtService.extractUsername(tokens.getAccessToken());
    User user = service.getUserByEmail(email);
    return ResponseEntity.ok(LoginResponse.builder()
            .userId(user.getId())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .email(user.getEmail())
            .title(user.getTitle())
            .phoneNumber(user.getPhoneNumber())
            .address(user.getAddress())
            .role(user.getRole())
            .message("Successfully!")
            .build());
  }

}
