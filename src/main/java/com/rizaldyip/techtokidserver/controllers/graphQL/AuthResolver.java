package com.rizaldyip.techtokidserver.controllers.graphQL;

import com.rizaldyip.techtokidserver.dtos.request.AdminOnboardReqDto;
import com.rizaldyip.techtokidserver.dtos.request.LoginReqDto;
import com.rizaldyip.techtokidserver.dtos.request.RegisterReqDto;
import com.rizaldyip.techtokidserver.dtos.response.AuthPayloadDto;
import com.rizaldyip.techtokidserver.dtos.response.UserResDto;
import com.rizaldyip.techtokidserver.helpers.Claims;
import com.rizaldyip.techtokidserver.services.AuthService;
import jakarta.mail.MessagingException;
import lombok.extern.java.Log;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Log
public class AuthResolver {
    private final AuthService authService;

    public AuthResolver(AuthService authService) {
        this.authService = authService;
    }

    @MutationMapping(value = "userSignIn")
    public AuthPayloadDto userSignIn(@Argument LoginReqDto input) {
        return authService.userSignIn(input);
    }

    @MutationMapping(value = "adminSignIn")
    public AuthPayloadDto adminSignIn(@Argument LoginReqDto input) {
        return authService.adminSignIn(input);
    }

    @MutationMapping(value = "signUp")
    public AuthPayloadDto signUp(@Argument RegisterReqDto input) throws MessagingException, IOException {
        return authService.signUp(input);
    }

    @MutationMapping(value = "confirmEmail")
    @PreAuthorize("isAuthenticated()")
    public AuthPayloadDto confirmEmail() {
        var claims = Claims.getClaimsFromJwt();
        String email = (String) claims.get("email");

        return authService.emailConfirmation(email);
    }

    @MutationMapping(value = "resetPasswordRequest")
    public AuthPayloadDto resetPasswordReq(@Argument String email) throws MessagingException, IOException {
        return authService.resetPasswordReq(email);
    }

    @MutationMapping(value = "resetPassword")
    @PreAuthorize("isAuthenticated()")
    public AuthPayloadDto resetPassword(@Argument String password) {
        var claims = Claims.getClaimsFromJwt();
        String email = (String) claims.get("email");
        return authService.resetPassword(email, password);
    }

    @MutationMapping(value = "addAdmin")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_SUPER_ADMIN')")
    public AuthPayloadDto addAdmin(@Argument String email) throws MessagingException, IOException {
        return authService.addAdmin(email);
    }

    @MutationMapping(value = "adminOnboarding")
    @PreAuthorize("isAuthenticated()")
    public UserResDto adminOnboarding(@Argument AdminOnboardReqDto input) {
        var claims = Claims.getClaimsFromJwt();
        log.info("user claims -> " + claims.toString());
        String email = (String) claims.get("sub");

        return authService.adminOnboarding(email, input);
    }
}
