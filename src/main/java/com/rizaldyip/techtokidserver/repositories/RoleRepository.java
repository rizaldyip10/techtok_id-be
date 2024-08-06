package com.rizaldyip.techtokidserver.repositories;

import com.rizaldyip.techtokidserver.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
