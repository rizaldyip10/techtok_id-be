package com.rizaldyip.techtokidserver.dtos.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResDto {
    private Long id;
    private String name;
    private String email;
    private int phone;
    private String profileImg;
    private List<RoleResDto> roles;
}
