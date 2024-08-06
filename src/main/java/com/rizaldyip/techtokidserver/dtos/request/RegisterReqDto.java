package com.rizaldyip.techtokidserver.dtos.request;

import com.rizaldyip.techtokidserver.entities.Role;
import lombok.Data;

@Data
public class RegisterReqDto {
    private String name;
    private String email;
    private String password;
    private Long roleId;
}
