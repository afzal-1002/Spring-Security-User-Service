package com.afzora.nova.cart.service;

import com.afzora.nova.cart.dto.request.LoginRequest;
import com.afzora.nova.cart.dto.request.RefreshTokenRequest;
import com.afzora.nova.cart.dto.request.RegisterRequest;
import com.afzora.nova.cart.dto.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
