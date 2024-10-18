package com.toren.shewstringbe.controller;

import com.toren.shewstringbe.dto.userdto.UserLoggedInDto;
import com.toren.shewstringbe.dto.userdto.UserLoginDto;
import com.toren.shewstringbe.dto.userdto.UserRegisterDto;
import com.toren.shewstringbe.mapper.UserProfileMapper;
import com.toren.shewstringbe.models.UserProfile;
import com.toren.shewstringbe.service.AuthenticationService;
import com.toren.shewstringbe.service.JwtService;
import com.toren.shewstringbe.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final JwtService jwtService;
    private final UserProfileMapper userProfileMapper;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService, JwtService jwtService,
                                UserProfileMapper userProfileMapper, AuthenticationService authenticationService) {
        this.userProfileService = userProfileService;
        this.jwtService = jwtService;
        this.userProfileMapper = userProfileMapper;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoggedInDto> loginUser(@RequestBody UserLoginDto loginDto) throws Exception {
        log.info(loginDto.toString());
        return ResponseEntity.ok(getUserAndJwt(authenticationService.loginAuth(loginDto).getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<UserLoggedInDto> registerUser(@RequestBody UserRegisterDto registerDto) throws Exception {
        log.info("hello");

        UserProfile userProfile = authenticationService.signup(registerDto);

        log.info(userProfile.toString());

        UserLoggedInDto userLoggedInDto = getUserAndJwt(userProfile.getUsername());

        log.info(userLoggedInDto.toString());

        return ResponseEntity.ok(userLoggedInDto);
    }

    private UserLoggedInDto getUserAndJwt(String userName) throws Exception {
        String token = jwtService.generateToken(userName);
        UserProfile userProfile;
        try {
            userProfile = userProfileService.getUserProfileByUserName(userName).orElseThrow();
        } catch (EntityNotFoundException e) {
            throw new Exception("User not found", e);
        }

        return userProfileMapper.fromUserProfileToLoggedInDto(userProfile, token);
    }
}
