package com.rizaldyip.techtokidserver.services;

import com.rizaldyip.techtokidserver.dtos.response.RoleResDto;
import com.rizaldyip.techtokidserver.entities.Role;

import java.util.List;

public interface RoleService {
    Role getRole(Long id);
    List<RoleResDto> getRoles();
}
