package com.afzora.nova.cart.service;

import com.afzora.nova.cart.dto.request.CreateUserRequest;
import com.afzora.nova.cart.dto.request.UpdateUserRequest;
import com.afzora.nova.cart.dto.response.UserProfileResponse;
import com.afzora.nova.cart.dto.response.UserResponse;

import java.util.List;

public interface UsersService {
    UserResponse findByUserName(String username);
    UserResponse findByEmail(String email);
    UserResponse findById(Long id);
    UserProfileResponse getProfile();
    List<UserResponse> getAllUsers();
    UserResponse createCustomer(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    boolean existsByUserName(String username);
}