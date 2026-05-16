package com.afzora.nova.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

//@Data
//@Builder
//@RequiredArgsConstructor
//@AllArgsConstructor
//public class AuthResponse {
//    private String accessToken;
//    private String refreshToken;
//    private String tokenType;
//    private Long expiresIn;
//    private Set<String> roles;
//}

@Builder
public record AuthResponse( String accessToken,  String refreshToken,
                            String tokenType, Long expiresIn, Set<String> roles) {
}