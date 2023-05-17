package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.AuthenticationRequest;
import com.SIEBS.ITCompany.dto.AuthenticationResponse;
import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.dto.PasswordlessAuthenticationRequest;
import com.SIEBS.ITCompany.dto.RegisterRequest;
import com.SIEBS.ITCompany.model.MagicLink;
import com.SIEBS.ITCompany.model.Token;
import com.SIEBS.ITCompany.repository.AddressRepository;
import com.SIEBS.ITCompany.repository.TokenRepository;
import com.SIEBS.ITCompany.enumerations.TokenType;
import com.SIEBS.ITCompany.model.User;
import com.SIEBS.ITCompany.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;

  private final AddressRepository addressRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final MagicLinkService magicLinkService;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  public MessageResponse register(RegisterRequest request) {
    Optional<User> tmp = repository.findByEmail(request.getEmail());
    if (!tmp.isPresent()){
      var user = User.builder()
              .firstname(request.getFirstname())
              .lastname(request.getLastname())
              .email(request.getEmail())
              .password(passwordEncoder.encode(request.getPassword()))
              .phoneNumber(request.getPhoneNumber())
              .isApproved(false)
              .title(request.getTitle())
              .address(request.getAddress())
              .role(request.getRole())
              .build();
      var address =addressRepository.save(request.getAddress());
      var savedUser = repository.save(user);
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return MessageResponse.builder()
              .message("Success!")
              .build();
    }
    return MessageResponse.builder()
            .message("User with that email already exist!").build();
  }

  public AuthenticationResponse generateAccessAndRefresToken(String token) {
    boolean isTokenValid = isTokenForPasswordlessLoginValid(token);
    if (isTokenValid) {
      String email = jwtService.extractUsername(token);
      var user = repository.findByEmail(email).orElse(null);
      if (user == null) {
        return null;
      }
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .build();
    }
    return null;
  }

  public String generateAndSendToken(PasswordlessAuthenticationRequest request){
    var user = repository.findByEmail(request.getEmail()).orElse(null);
    if(user == null){
      return "User not found!";
    }
    var jwtToken = jwtService.generateTokenForPasswordlessLogin(user);
    String url = "http://localhost:8081/api/v1/auth/authenticate?token=" + jwtToken;
    magicLinkService.Save(MagicLink.builder().used(false).token(jwtToken).build());
    System.out.println(url);
    //ovjde ide slanje linka na mejl
    return url;
  }

  private boolean isTokenForPasswordlessLoginValid(String token){
    if(magicLinkService.isTokenUsed(token)){
      return false;
    }else if(jwtService.isTokenExpired(token)){
      return false;
    }
    return true;
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
