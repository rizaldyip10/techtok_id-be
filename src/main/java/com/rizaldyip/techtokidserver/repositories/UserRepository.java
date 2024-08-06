package com.rizaldyip.techtokidserver.repositories;

import com.rizaldyip.techtokidserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
