package com.afzora.nova.cart.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public interface JwtService {
      String generateAccessToken( UserDetails userDetails);
      String generateRefreshToken( UserDetails userDetails);
      String extractUsername(String token);
      Claims extractAllClaims(String token);
      boolean isTokenValid( String token, UserDetails userDetails);
      boolean isRefreshTokenValid(String token, UserDetails userDetails);
      boolean isTokenExpired(String token);
      SecretKey getSignInKey();
      String refreshAccessToken(String refreshToken);
}