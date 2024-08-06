package com.rizaldyip.techtokidserver.services;

import com.rizaldyip.techtokidserver.dtos.request.UpdateProfileReqDto;
import com.rizaldyip.techtokidserver.dtos.response.UserResDto;
import com.rizaldyip.techtokidserver.entities.User;

import java.util.Optional;

public interface UserService {
    UserResDto getUserProfile(String email);
    UserResDto updateUserProfile(String email, UpdateProfileReqDto reqDto);
    void deleteUser(String email);
}
