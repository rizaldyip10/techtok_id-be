package com.rizaldyip.techtokidserver.services.impl;

import com.rizaldyip.techtokidserver.dtos.response.RoleResDto;
import com.rizaldyip.techtokidserver.entities.Role;
import com.rizaldyip.techtokidserver.exceptions.DataNotFoundExceptions;
import com.rizaldyip.techtokidserver.repositories.RoleRepository;
import com.rizaldyip.techtokidserver.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundExceptions("Role not found"));
    }

    @Override
    public List<RoleResDto> getRoles() {
        return roleRepository.findAll().stream().map(Role::toRoleResDto).toList();
    }
}
