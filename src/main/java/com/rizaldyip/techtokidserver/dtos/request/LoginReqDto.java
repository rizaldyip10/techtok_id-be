package com.rizaldyip.techtokidserver.dtos.request;

import lombok.Data;

@Data
public class LoginReqDto {
    private String email;
    private String password;
}
