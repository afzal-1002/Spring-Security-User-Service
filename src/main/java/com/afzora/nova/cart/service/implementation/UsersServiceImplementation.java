package com.afzora.nova.cart.service.implementation;

import com.afzora.nova.cart.dto.request.CreateUserRequest;
import com.afzora.nova.cart.dto.request.UpdateUserRequest;
import com.afzora.nova.cart.dto.response.UserProfileResponse;
import com.afzora.nova.cart.dto.response.UserResponse;
import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.entity.Users;
import com.afzora.nova.cart.exception.UserAlreadyExistsException;
import com.afzora.nova.cart.mapper.UsersMapper;
import com.afzora.nova.cart.repository.UsersRepository;
import com.afzora.nova.cart.service.RoleService;
import com.afzora.nova.cart.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersServiceImplementation implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersMapper usersMapper;
    private final RoleService roleService;

    public UsersServiceImplementation(UsersRepository usersRepository,
                                      PasswordEncoder passwordEncoder,
                                      UsersMapper usersMapper,
                                      RoleService roleService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersMapper = usersMapper;
        this.roleService = roleService;
    }

    @Override
    public UserResponse findByUserName(String username) {
        Optional<Users> user = usersRepository.findByUserName(username);
        return this.usersMapper.mapToResponse(user.orElseThrow(() ->
                new RuntimeException("User not found")));
    }

    @Override
    public UserResponse findByEmail(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);
        return this.usersMapper.mapToResponse(user.orElseThrow(() ->
                new RuntimeException("User not found")));
    }

    @Override
    public UserResponse findById(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
        return this.usersMapper.mapToResponse(user);
    }

    @Override
    public UserProfileResponse getProfile() {
        String username = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Users user = usersRepository.findByUserName(username).orElseThrow(() ->
                new RuntimeException("User not found"));
        return this.usersMapper.mapToProfileResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return usersRepository.findAll().stream().map(this.usersMapper::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse createCustomer(CreateUserRequest request) {

        if (existsByEmail(request.email())) {throw
                new UserAlreadyExistsException("Email already exists");}

        if (existsByUserName(request.userName())) {throw
                new UserAlreadyExistsException("Username already exists");}

        Set<String> roleNames = request.roles();

        if (roleNames == null || roleNames.isEmpty()) {roleNames = Set.of("CUSTOMER");}

        Set<Role> roles = roleService.findRolesByNames(roleNames);

        validateRoles(roles);

        Users user = Users.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .userName(request.userName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        Users savedUser = usersRepository.save(user);

        return this.usersMapper.mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        Users user = usersRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        Users updatedUser = usersRepository.save(user);

        return this.usersMapper.mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
        usersRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String username) {
        return usersRepository.existsByUserName(username);
    }

    private void validateRoles(Set<Role> roles) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getAuthorities().isEmpty()) {
            if (!roles.stream().map(Role::getName).collect(
                    Collectors.toSet()).equals(Set.of("CUSTOMER"))) {
                throw new RuntimeException("Only CUSTOMER role allowed");
            }
            return;
        }

        String currentRole = authentication.getAuthorities().iterator().next().getAuthority();

        assert currentRole != null;
        if (currentRole.equals("ROLE_CUSTOMER")) {
            if (!roles.stream().map(Role::getName).collect(
                    Collectors.toSet()).equals(Set.of("CUSTOMER"))) {
                throw new RuntimeException("CUSTOMER cannot create ADMIN or MANAGER");
            }
        }

        if (currentRole.equals("ROLE_MANAGER")) {
            if (roles.stream().map(Role::getName).collect(
                    Collectors.toSet()).contains("ADMIN")) {
                throw new RuntimeException("MANAGER cannot create ADMIN");
            }
        }
    }
}