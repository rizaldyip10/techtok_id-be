package com.rizaldyip.techtokidserver.controllers.graphQL;

import com.rizaldyip.techtokidserver.dtos.response.UserResDto;
import com.rizaldyip.techtokidserver.helpers.Claims;
import com.rizaldyip.techtokidserver.repositories.UserRepository;
import com.rizaldyip.techtokidserver.services.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserResolver {
    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping(value = "profile")
    public UserResDto profile() {
        var claims = Claims.getClaimsFromJwt();
        String email = (String) claims.get("sub");
        return userService.getUserProfile(email);
    }
}
