package com.afzora.nova.cart.dto.request;

import java.util.Set;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String userName,
        String email,
        String phoneNumber,
        String password,
        Set<String> roles
) { }