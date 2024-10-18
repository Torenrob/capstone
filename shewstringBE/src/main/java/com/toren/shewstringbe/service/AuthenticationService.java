package com.toren.shewstringbe.service;

import com.toren.shewstringbe.dto.userdto.UserLoginDto;
import com.toren.shewstringbe.dto.userdto.UserRegisterDto;
import com.toren.shewstringbe.mapper.UserProfileMapper;
import com.toren.shewstringbe.models.UserProfile;
import com.toren.shewstringbe.repository.UserProfileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {
    private final UserProfileRepo userProfileRepo;
    private final AuthenticationManager authenticationManager;
    private final UserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserProfileRepo userProfileRepo, AuthenticationManager authenticationManager,
                                UserProfileService userProfileService, UserProfileMapper userProfileMapper,
                                PasswordEncoder passwordEncoder) {
        this.userProfileRepo = userProfileRepo;
        this.authenticationManager = authenticationManager;
        this.userProfileService = userProfileService;
        this.userProfileMapper = userProfileMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfile signup(UserRegisterDto userRegisterDto) {
        userRegisterDto.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        
        return userProfileService
                .createUserProfile(userProfileMapper.fromUserRegisterDtoToUserProfile(userRegisterDto));
    }

    public UserProfile loginAuth(UserLoginDto userLoginDto) {
        log.info("About to authenticate");
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(),userLoginDto.getPassword()));

        return userProfileRepo.findUserProfileByUsername(userLoginDto.getUsername()).orElseThrow();
    }
}
