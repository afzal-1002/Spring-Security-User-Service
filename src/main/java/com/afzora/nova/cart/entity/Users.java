package com.afzora.nova.cart.entity;

import com.afzora.nova.cart.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable( name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;

    private Boolean enabled;

    private Boolean accountNonLocked;

    private Integer failedLoginAttempts;

    private LocalDateTime lockTime;

    private Boolean emailVerified;

    private Boolean phoneVerified;

    private LocalDateTime passwordChangedAt;

    private LocalDateTime lastLoginAt;

    private String profileImageUrl;

    private String provider;

    private String providerId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        this.enabled = true;

        this.accountNonLocked = true;

        this.failedLoginAttempts = 0;

        this.emailVerified = false;

        this.phoneVerified = false;
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}