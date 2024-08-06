package com.rizaldyip.techtokidserver.services;

import com.rizaldyip.techtokidserver.dtos.request.LoginReqDto;
import com.rizaldyip.techtokidserver.dtos.request.RegisterReqDto;
import com.rizaldyip.techtokidserver.dtos.response.AuthPayloadDto;
import com.rizaldyip.techtokidserver.entities.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface AuthService {
    String generateToken(Authentication authentication, int expiration, String keyPrefix);
    String generateTokenWOAuth(User user, int expiration, String keyPrefix);
    AuthPayloadDto signIn(LoginReqDto loginReqDto);
    AuthPayloadDto signUp(RegisterReqDto registerReqDto) throws MessagingException, IOException;
    AuthPayloadDto emailConfirmation(String email);
    AuthPayloadDto resetPasswordReq(String email) throws MessagingException, IOException;
    AuthPayloadDto resetPassword(String email, String password);
}
