package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.*;
import com.SIEBS.ITCompany.model.*;
import com.SIEBS.ITCompany.repository.*;
import com.SIEBS.ITCompany.enumerations.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final RoleRepository roleRepository;

  private final AddressRepository addressRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final MagicLinkService magicLinkService;
  private final EmailService emailService;
  private final UserRoleRepository userRoleRepository;
  private final KeystoreService keystoreService;
  private User loggedUser;

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try{
      if(!repository.findByEmail(request.getEmail()).orElse(null).isApproved()){
        return AuthenticationResponse
                .builder()
                .loginResponse(LoginResponse
                        .builder()
                        .message("Your account are not approved by administrator!")
                        .build())
                .build();
      }
    }catch (Exception e){

    }
    try{
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
    }catch(BadCredentialsException e){
      return AuthenticationResponse
              .builder()
              .loginResponse(LoginResponse
                      .builder()
                      .message("Email or password are not correct!")
                      .build())
              .build();
    }
    //do ovog dijela koda je dosao samo ako je tacna lozinka i email
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();

    if (user.isUsing2FA()) {
      Totp totp = new Totp(user.getSecret());
      if (!isValidLong(request.getCode()) || !totp.verify(request.getCode())) {
        return AuthenticationResponse
                .builder()
                .loginResponse(LoginResponse
                        .builder()
                        .message("Validation code is not correct!")
                        .build())
                .build();
      }
    }

    loggedUser = user;
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    return AuthenticationResponse
            .builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .loginResponse(LoginResponse.builder()
                    .userId(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .title(user.getTitle())
                    .phoneNumber(user.getPhoneNumber())
                    .address(user.getAddress())
                    .role(new RoleDTO(user.getRole().getId(), user.getRole().getName()))
                    .message("Successfully!")
                    .build())
            .build();
  }

  private boolean isValidLong(String code) {
    try {
      Long.parseLong(code);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  public String encodeTitle(RegisterRequest request) throws Exception {
    SecretKey key = keystoreService.generateKey();
    String encriptedValue = keystoreService.encrypt(request.getTitle(), key);
    keystoreService.addKey(request.getEmail(), request.getPhoneNumber(), key);

    return encriptedValue;
  }


  public MessageResponse register(RegisterRequest request) throws Exception {
    Optional<User> tmp = repository.findByEmail(request.getEmail());
    Role role = roleRepository.findByName(request.getRole());
    //Provjera jedinstvenosti mejla i provjera da li je korisniku sa navedenim emailom odbijen zahtjev za registraciju u posljednjih 10min

    if (!tmp.isPresent()){
      var user = User.builder()
              .firstname(request.getFirstname())
              .lastname(request.getLastname())
              .email(request.getEmail())
              .password(passwordEncoder.encode(request.getPassword()))
              .phoneNumber(request.getPhoneNumber())
              .isApproved(false)
              .registrationDate(null)
              .title(encodeTitle(request))
              .address(request.getAddress())
              .role(role)
              .roles(new ArrayList<>())
              .build();
      var address =addressRepository.save(request.getAddress());
      var savedUser = repository.save(user);
      var primalRole = UserRole.builder()
              .user(user)
              .role(role)
              .build();
      userRoleRepository.save(primalRole);
      List<UserRole> userRoles = new ArrayList<>();
      for (String roleString:request.getRoles()) {
        Role r = roleRepository.findByName(roleString);
        var userRole = UserRole.builder()
                .user(user)
                .role(r)
                .build();
        userRoleRepository.save(userRole);
        userRoles.add(userRole);
      }
      user.setRoles(userRoles);
      return MessageResponse.builder()
              .message("Success!").build();
    }else{
      User user = tmp.get();
      if (user.isApproved()==true){
        return MessageResponse.builder()
                .message("User with that email already exist!").build();
      }else{
        if (user.getRegistrationDate()==null){
          return MessageResponse.builder()
                  .message("Your request is waiting for the admin's response!").build();
        }else{
          Date checkDate = new Date(user.getRegistrationDate().getTime() + 10 * 60 * 1000);
          Date now = new Date();
          if (!checkDate.after(now)){
            User updatedUser = tmp.get();
            updatedUser.setFirstname(request.getFirstname());
            updatedUser.setLastname(request.getLastname());
            updatedUser.setPassword(request.getPassword());
            updatedUser.setPhoneNumber(request.getPhoneNumber());
            updatedUser.setRegistrationDate(null);
            updatedUser.setTitle(request.getTitle());
            updatedUser.setAddress(request.getAddress());
            updatedUser.setRole(role);

            addressRepository.save(request.getAddress());
            repository.update(updatedUser);
            return MessageResponse.builder()
                    .message("Request is successfully made again with these params!: " + user).build();

          }else{
            return MessageResponse.builder()
                    .message("You can not send new registration request until: " +checkDate + "!").build();
          }
        }
      }
    }
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
      revokeAllUserTokens(user);
      saveUserToken(user, jwtToken);
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
    String url = "https://localhost:8081/api/v1/auth/authenticate?token=" + jwtToken;
    magicLinkService.Save(MagicLink.builder().used(false).token(jwtToken).build());
    System.out.println(url);
    String message = "Hello " + user.getFirstname() + ", this is your access link: " + url;
    //ovjde ide slanje linka na mejl
    emailService.sendMail(user.getEmail(), "IT-Company: Passwordless login", url);
    return url;
  }

  public String generateTokenForRegistration(String email){
    var user = repository.findByEmail(email).orElse(null);
    var jwtToken = jwtService.generateTokenForPasswordlessLogin(user);
    String url = "https://localhost:8081/api/v1/auth/register/verificate?token=" + jwtToken +"&email="+email;
    magicLinkService.Save(MagicLink.builder().used(false).token(jwtToken).build());
    System.out.println(url);
    return url;
  }

  public boolean isTokenForPasswordlessLoginValid(String token){
    if(magicLinkService.isTokenUsed(token)){
      return false;
    }else if(jwtService.isTokenExpired(token)){
      return false;
    }
    return true;
  }

  public void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  public void revokeAllUserTokens(User user) {
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

  public TokensDTO getTokensFromRequest(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    String accessToken = "";
    String refreshToken = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("access_token")) {
          accessToken = cookie.getValue();
        }
        if (cookie.getName().equals("refresh_token")) {
          refreshToken = cookie.getValue();
        }
      }
    }
    return TokensDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public User getUserByEmail(String email){
    return repository.findByEmail(email).orElse(null);
  }

  public User getLoggedUser() {
    return loggedUser;
  }
}
