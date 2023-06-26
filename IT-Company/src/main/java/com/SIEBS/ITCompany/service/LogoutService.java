package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private final NotificationService notificationService;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {

    deleteAllCookies(response);
    notificationService.logoutLoggedUser();
    String jwt = getAccessTokenFromCookie(request);
    if(jwt == ""){
      return;
    }

    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
  }

  public String getAccessTokenFromCookie(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    String jwt = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("access_token")) {
          jwt = cookie.getValue();
        }
      }
    }
    return jwt;
  }
  public void deleteAllCookies(HttpServletResponse response){
    Cookie accessTokenCookie = new Cookie("access_token", null);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setDomain("localhost");
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(0);
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("refresh_token", null);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setDomain("localhost");
    refreshTokenCookie.setPath("/api/v1/auth/refresh-token");
    refreshTokenCookie.setMaxAge(0);
    response.addCookie(refreshTokenCookie);
  }
}
