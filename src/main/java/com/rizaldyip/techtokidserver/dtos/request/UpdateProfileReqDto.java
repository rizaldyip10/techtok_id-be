package com.rizaldyip.techtokidserver.dtos.request;

import lombok.Data;

@Data
public class UpdateProfileReqDto {
    private String name;
    private int phone;
    private String password;
    private String profileImg;
}
