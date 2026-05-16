package com.afzora.nova.cart.dto.request;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }