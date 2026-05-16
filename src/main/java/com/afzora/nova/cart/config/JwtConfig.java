package com.afzora.nova.cart.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtConfig {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.access-expiration}")
    private Long ACCESS_EXPIRATION;

    @Value("${jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;
}
