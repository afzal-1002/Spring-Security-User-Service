package com.afzora.nova.cart.dto.response;

import java.util.Set;

public record UserProfileResponse(
        String firstName,
        String lastName,
        String userName,
        String email,
        String phoneNumber,
        Set<String> roles
) { }
