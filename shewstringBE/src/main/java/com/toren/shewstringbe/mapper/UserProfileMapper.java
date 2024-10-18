package com.toren.shewstringbe.mapper;

import com.toren.shewstringbe.dto.userdto.UserLoggedInDto;
import com.toren.shewstringbe.dto.userdto.UserRegisterDto;
import com.toren.shewstringbe.models.UserProfile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public UserProfileMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserProfile fromUserRegisterDtoToUserProfile(UserRegisterDto userRegisterDto) {
        
        return modelMapper.map(userRegisterDto, UserProfile.class);
    }

    public UserLoggedInDto fromUserProfileToLoggedInDto(UserProfile userProfile, String token) {
        UserLoggedInDto userLoggedInDto = modelMapper.map(userProfile, UserLoggedInDto.class);

        userLoggedInDto.setToken(token);

        return userLoggedInDto;
    }
}
