package com.SIEBS.ITCompany.config;

import com.SIEBS.ITCompany.service.AuthenticationService;
import com.SIEBS.ITCompany.service.JwtService;
import com.SIEBS.ITCompany.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  //private final AuthenticationService authenticationService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    Cookie[] cookies = request.getCookies();
    String jwt = "";
    String refreshToken = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("access_token")) {
          jwt = cookie.getValue();
        }
        if (cookie.getName().equals("refresh_token")) {
          refreshToken = cookie.getValue();
        }
      }
    }
    final String userEmail;
    if (jwt == "" || refreshToken == "") {
      filterChain.doFilter(request, response);
      return;
    }

    if(jwtService.isTokenExpired(jwt)){
      if(jwtService.isTokenExpired(refreshToken)){
        filterChain.doFilter(request, response);
        return;
      }
      String accessToken = refreshAccessToken(refreshToken);
      Cookie accessTokenCookie = new Cookie("access_token", accessToken);
      accessTokenCookie.setHttpOnly(true);
      accessTokenCookie.setDomain("localhost");
      accessTokenCookie.setPath("/");
      response.addCookie(accessTokenCookie);
      jwt = accessToken;
    }

    userEmail = jwtService.extractUsername(jwt);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      /*var isTokenValid = tokenRepository.findByToken(jwt)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);*/
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }

  public String refreshAccessToken(String refreshToken) {
    final String username = jwtService.extractUsername(refreshToken);
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
    return jwtService.generateToken(new HashMap<>(), userDetails);
  }

  private String getTokenFromRequest(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    String accessToken = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("access_token")) {
          accessToken = cookie.getValue();
        }
      }
    }
    return accessToken;
  }
}
