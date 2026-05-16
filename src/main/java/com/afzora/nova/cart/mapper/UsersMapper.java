package com.afzora.nova.cart.mapper;

import com.afzora.nova.cart.dto.response.UserProfileResponse;
import com.afzora.nova.cart.dto.response.UserResponse;
import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.entity.Users;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsersMapper {

    public UserResponse mapToResponse(Users user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getEnabled()
        );
    }

    public UserProfileResponse mapToProfileResponse(Users user) {
        return new UserProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    public Users toUsers(UserResponse userResponse) {

        Set<Role> roles = userResponse.roles()
                .stream()
                .map(roleName -> Role.builder().name(roleName).build())
                .collect(Collectors.toSet());

        return Users.builder()
                .id(userResponse.id())
                .firstName(userResponse.firstName())
                .lastName(userResponse.lastName())
                .userName(userResponse.userName())
                .email(userResponse.email())
                .phoneNumber(userResponse.phoneNumber())
                .roles(roles)
                .enabled(userResponse.enabled())
                .build();
    }
}