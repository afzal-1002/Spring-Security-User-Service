package com.afzora.nova.cart.service;

import com.afzora.nova.cart.entity.Users;
import org.springframework.stereotype.Service;

@Service
public interface UsersService  {

    Users findByUserName(String username);
    Users createUser();
    Users getAllUsers();
    Users deleteUser();
}
