package com.afzora.nova.cart.service.implementation;

import com.afzora.nova.cart.dto.request.LoginRequest;
import com.afzora.nova.cart.dto.request.RefreshTokenRequest;
import com.afzora.nova.cart.dto.request.RegisterRequest;
import com.afzora.nova.cart.dto.response.AuthResponse;
import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.entity.Users;
import com.afzora.nova.cart.mapper.AuthMapper;
import com.afzora.nova.cart.repository.UsersRepository;
import com.afzora.nova.cart.security.CustomUserDetails;
import com.afzora.nova.cart.service.AuthService;
import com.afzora.nova.cart.service.JwtService;
import com.afzora.nova.cart.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImplementation implements AuthService {

    @Value("${jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;

    @Value("${jwt.access-expiration}")
    private Long ACCESS_EXPIRATION;

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final AuthMapper authMapper;

    public AuthServiceImplementation(UsersRepository userRepository, PasswordEncoder passwordEncoder,
                                     AuthenticationManager authenticationManager,
                                     JwtService jwtService, RoleService roleService,
                                     AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.roleService = roleService;
        this.authMapper = authMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        Set<String> roleNames = request.roles();
        if (roleNames == null || roleNames.isEmpty()) {roleNames = Set.of("CUSTOMER");}
        Set<Role> roles = roleService.findRolesByNames(roleNames);
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

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(user));
        String refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(user));

        return authMapper.buildAuthResponse(user, accessToken, refreshToken, ACCESS_EXPIRATION);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                        request.userName(),  request.password() ));

        Users user = userRepository.findByUserName(request.userName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(user));
        String refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(user));

        return authMapper.buildAuthResponse(user, accessToken, refreshToken, ACCESS_EXPIRATION);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.refreshToken();
        String username = jwtService.extractUsername(refreshToken);

        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String accessToken = jwtService.refreshAccessToken(refreshToken);

        return authMapper.buildAuthResponse(user, accessToken, refreshToken, REFRESH_EXPIRATION);
    }

}
