package com.afzora.nova.cart.repository;

import com.afzora.nova.cart.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String userName);
    Optional<Users> findByEmail(String userEmail);
    boolean existsByEmail(String userEmail);
    boolean existsByUserName(String userName);

}
