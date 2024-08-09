package com.rizaldyip.techtokidserver.dtos.request;

import lombok.Data;

@Data
public class AdminOnboardReqDto {
    private String name;
    private String password;
    private Long roleId;
}
