package com.rizaldyip.techtokidserver.dtos.response;

import lombok.Data;

@Data
public class AuthPayloadDto {
    private String message;
    private String token;
}
