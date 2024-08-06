package com.rizaldyip.techtokidserver.services.impl;


import com.rizaldyip.techtokidserver.dtos.request.UpdateProfileReqDto;
import com.rizaldyip.techtokidserver.dtos.response.UserResDto;
import com.rizaldyip.techtokidserver.entities.User;
import com.rizaldyip.techtokidserver.exceptions.ApplicationException;
import com.rizaldyip.techtokidserver.exceptions.DataNotFoundExceptions;
import com.rizaldyip.techtokidserver.repositories.UserRepository;
import com.rizaldyip.techtokidserver.services.AuthService;
import com.rizaldyip.techtokidserver.services.RoleService;
import com.rizaldyip.techtokidserver.services.UserService;
import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResDto getUserProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found"))
                .toUserResDto();
    }

    @Override
    public UserResDto updateUserProfile(String email, UpdateProfileReqDto reqDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found"));

        if (!user.getEmail().equals(email)) {
            throw new ApplicationException("Email does not match");
        }
        user.setName(reqDto.getName());
        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));
        user.setPhone(reqDto.getPhone());
        user.setProfileImg(reqDto.getProfileImg());

        return userRepository.save(user).toUserResDto();
    }

    @Override
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found"));
        userRepository.delete(user);
    }
}
