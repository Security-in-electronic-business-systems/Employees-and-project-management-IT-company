package com.SIEBS.ITCompany.controller;
import com.SIEBS.ITCompany.dto.AuthenticationRequest;
import com.SIEBS.ITCompany.dto.AuthenticationResponse;
import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.dto.RegisterRequest;
import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.service.*;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final UserService userService;
  private final JwtService jwtService;
  private final MagicLinkService magicLinkService;
  private final PermissionService permissionService;


  @PostMapping("/authenticate")

  public ResponseEntity<LoginResponse> authenticate(
          @RequestBody AuthenticationRequest request,
          HttpServletResponse response

  ) {
    AuthenticationResponse authResponse = service.authenticate(request);

    if (authResponse != null) {
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
    URI waitRoomUri = URI.create("http://localhost:3000/wait-room");
    URI tokenExpiredUri = URI.create("http://localhost:3000/token-expired");
    if(authResponse != null){
      magicLinkService.setUsedByToken(token);

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
              .location(waitRoomUri)
              .body(authResponse);
    }

    //authResponse.getLoginResponse().setMessage("Link was expired!");
    return ResponseEntity.status(HttpStatus.FOUND)
            .location(tokenExpiredUri)
            .body(authResponse);

  }

  @GetMapping("/register/verificate")
  public ResponseEntity<MessageResponse> verificateRegistration(
          @RequestParam("token") String token,@RequestParam("email") String email,
          HttpServletRequest request,
          HttpServletResponse response
  ) {
    URI login = URI.create("http://localhost:3000/login");
    URI tokenExpiredUri = URI.create("http://localhost:3000/token-expired");
    if(service.isTokenForPasswordlessLoginValid(token)){
      magicLinkService.setUsedByToken(token);
      userService.updateRegistrationDate(email, new Date());
      userService.approveUser(email);
      MessageResponse message = new MessageResponse("Link is valid, you are registered!");

      return ResponseEntity.status(HttpStatus.FOUND)
              .location(login)
              .body(message);
    }
    MessageResponse message = new MessageResponse("Link is not valid!");
    return ResponseEntity.status(HttpStatus.FOUND)
            .location(tokenExpiredUri)
            .body(message);

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
    return new ResponseEntity<>(service.register(request), HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PreAuthorize("@permissionService.hasPermission('GET_LOGIN_RESPONSE')")
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
