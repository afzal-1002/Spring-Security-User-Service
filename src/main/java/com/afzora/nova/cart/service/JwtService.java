package com.afzora.nova.cart.service;

public interface JwtService {
      String  generateToken();
      String  validateToken();
      String extractUsername();
      String isTokenExpired();
}
