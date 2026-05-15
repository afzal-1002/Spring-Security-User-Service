package com.afzora.nova.cart.security;

import com.afzora.nova.cart.entity.Users;
import com.afzora.nova.cart.service.UsersService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersService usersService;

    public CustomUserDetailsService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        Users user = usersService.findByUserName(username);
        if(user == null)
            throw new UsernameNotFoundException("User Not found with this name " + username);

        return new CustomUserDetails(user);
    }
}