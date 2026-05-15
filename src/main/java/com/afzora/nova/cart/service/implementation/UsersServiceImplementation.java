package com.afzora.nova.cart.service.implementation;

import com.afzora.nova.cart.entity.Users;
import com.afzora.nova.cart.repository.UsersRepository;
import com.afzora.nova.cart.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersServiceImplementation implements UsersService {

    private final UsersRepository usersRepository;

    public UsersServiceImplementation(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Users findByUserName(String username) {
        Optional<Users>  user = this.usersRepository.findByUserName(username);
        return user.orElse(null);
    }

    @Override
    public Users createUser() {
        return null;
    }

    @Override
    public Users getAllUsers() {
        return null;
    }

    @Override
    public Users deleteUser() {
        return null;
    }
}
