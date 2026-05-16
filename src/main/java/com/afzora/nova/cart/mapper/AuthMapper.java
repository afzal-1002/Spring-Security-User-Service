package com.afzora.nova.cart.mapper;

import com.afzora.nova.cart.dto.response.AuthResponse;
import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.entity.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthMapper {

    public AuthResponse buildAuthResponse(Users user, String accessToken,
                                          String refreshToken, Long EXPIRATION) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(EXPIRATION)
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
