package com.afzora.nova.cart.config;


import com.afzora.nova.cart.jwtauthfilter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationConfig authenticationConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationConfig authenticationConfig) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationConfig = authenticationConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated());

        http.formLogin(AbstractHttpConfigurer::disable);
        http.authenticationProvider(authenticationConfig.authenticationProvider());
        http.addFilterBefore( jwtAuthenticationFilter,  UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}