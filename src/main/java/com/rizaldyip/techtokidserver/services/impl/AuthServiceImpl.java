package com.rizaldyip.techtokidserver.services.impl;

import com.rizaldyip.techtokidserver.dtos.request.LoginReqDto;
import com.rizaldyip.techtokidserver.dtos.request.RegisterReqDto;
import com.rizaldyip.techtokidserver.dtos.response.AuthPayloadDto;
import com.rizaldyip.techtokidserver.entities.User;
import com.rizaldyip.techtokidserver.entities.UserAuth;
import com.rizaldyip.techtokidserver.exceptions.ApplicationException;
import com.rizaldyip.techtokidserver.exceptions.DataNotFoundExceptions;
import com.rizaldyip.techtokidserver.repositories.AuthRedisRepository;
import com.rizaldyip.techtokidserver.repositories.UserRepository;
import com.rizaldyip.techtokidserver.services.AuthService;
import com.rizaldyip.techtokidserver.services.EmailService;
import com.rizaldyip.techtokidserver.services.RoleService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final AuthRedisRepository authRedisRepository;
    private final RoleService roleService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserRepository userRepository, AuthRedisRepository authRedisRepository, RoleService roleService, EmailService emailService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.authRedisRepository = authRedisRepository;
        this.roleService = roleService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String generateToken(Authentication authentication, int expiration, String keyPrefix) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new DataNotFoundExceptions("User not found"));

        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJWTKey(authentication.getName(), keyPrefix);
        if (existingKey != null) {
            log.info("Token already exist for user: {}", authentication.getName());
            return existingKey;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiration, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("image", user.getProfileImg())
                .claim("name", user.getName())
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        authRedisRepository.saveJWTKey(authentication.getName(), jwt, keyPrefix, expiration);
        return jwt;
    }

    @Override
    public String generateTokenWOAuth(User user, int expiration, String keyPrefix) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiration, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("purpose", "reset_password")
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        authRedisRepository.saveJWTKey(user.getEmail(), jwt, keyPrefix, expiration);
        return jwt;
    }

    @Override
    public AuthPayloadDto signIn(LoginReqDto loginReqDto) {

        Optional<User> isUserExist = userRepository.findByEmail(loginReqDto.getEmail());
        if (isUserExist.isEmpty()) {
            throw new DataNotFoundExceptions("User not found");
        }
        if (!isUserExist.get().isVerified()) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "User is not verified");
        }

        log.info("Login request received for user: {}", loginReqDto.getEmail());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAuth userDetails = (UserAuth) authentication.getPrincipal();


        log.info("Principal user: {}", userDetails.getUsername());
        log.info("Token requested for user: {} with role {}", userDetails.getUsername(), userDetails.getAuthorities().toArray()[0]);
        String token = generateToken(authentication, 3, "techtokidserver:jwt:string");

        AuthPayloadDto resDto = new AuthPayloadDto();
        resDto.setMessage("Login success");
        resDto.setToken(token);

        return resDto;
    }

    @Override
    @Transactional
    public AuthPayloadDto signUp(RegisterReqDto registerReqDto) throws MessagingException, IOException {
        User user = new User();
        Optional<User> isUserExist = userRepository.findByEmail(registerReqDto.getEmail());
        if (isUserExist.isPresent()) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Email already exists");
        }
        var password = passwordEncoder.encode(registerReqDto.getPassword());
        var role = roleService.getRole(registerReqDto.getRoleId());
        String profilePicture = "https://ui-avatars.com/api/?name=" +
                registerReqDto.getName().replace(" ", "+") +
                "&background=ff782c&color=fff";
        user.setEmail(registerReqDto.getEmail());
        user.setPassword(password);
        user.setName(registerReqDto.getName());
        user.setRoles(List.of(role));
        user.setProfileImg(profilePicture);
        user.setVerified(false);
        userRepository.save(user);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(registerReqDto.getEmail(), registerReqDto.getPassword()));
        String token = generateToken(authentication, 1, "techtokid:emailconfirmation:string");

        String htmlPath = "C:/Users/idyog/OneDrive/Documents/techtok.id_revamp/server/techtokIdServer/src/main/resources/email-template/register-email.html";

        emailService.sendEmail(registerReqDto.getEmail(), token, htmlPath);

        var response = new AuthPayloadDto();
        response.setMessage("Registration success");
        response.setToken(token);

        return response;
    }

    @Override
    public AuthPayloadDto emailConfirmation(String email) {
        String keyPrefix = "techtokid:emailconfirmation:string";
        var isKeyExist = authRedisRepository.getJWTKey(email, keyPrefix);
        if (isKeyExist == null) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You already confirmed your email or your time to confirm your email has expired");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundExceptions("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        authRedisRepository.deleteJWTKey(email, keyPrefix);

        var response = new AuthPayloadDto();
        response.setMessage("Email confirmation success");

        return response;
    }

    @Override
    public AuthPayloadDto resetPasswordReq(String email) throws MessagingException, IOException {
        Optional<User> isUserExist = userRepository.findByEmail(email);
        if (isUserExist.isEmpty()) {
            throw new DataNotFoundExceptions("User not found");
        }

        var isKeyExist = authRedisRepository.getJWTKey(email, "techtokid:resetpassword:string");
        if (isKeyExist != null) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You have requested to reset your password, please try again after one hour");
        }

        String token = generateTokenWOAuth(isUserExist.get(), 1, "techtokid:resetpassword:string");

        String htmlPath = "C:/Users/idyog/OneDrive/Documents/techtok.id_revamp/server/techtokIdServer/src/main/resources/email-template/reset-password.html";
        emailService.sendEmail(isUserExist.get().getEmail(), token, htmlPath);

        var response = new AuthPayloadDto();
        response.setMessage("Please check your email to reset your password");
        response.setToken(token);

        return response;
    }

    @Override
    public AuthPayloadDto resetPassword(String email, String password) {
        Optional<User> isUserExist = userRepository.findByEmail(email);
        if (isUserExist.isEmpty()) {
            throw new DataNotFoundExceptions("User not found");
        }

        var isKeyExist = authRedisRepository.getJWTKey(email, "techtokid:resetpassword:string");
        if (isKeyExist == null) {
            throw new ApplicationException(HttpStatus.FORBIDDEN, "You already changed your password or your time to reset password has expired");
        }

        var newPassword = passwordEncoder.encode(password);
        isUserExist.get().setPassword(newPassword);
        userRepository.save(isUserExist.get());

        authRedisRepository.deleteJWTKey(email, "techtokid:resetpassword:string");

        var response = new AuthPayloadDto();
        response.setMessage("Reset password success");

        return response;
    }
}
