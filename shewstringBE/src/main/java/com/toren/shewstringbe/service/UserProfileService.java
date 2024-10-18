package com.toren.shewstringbe.service;

import com.toren.shewstringbe.models.UserProfile;
import com.toren.shewstringbe.repository.UserProfileRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileService implements UserDetailsService {

    private final UserProfileRepo userProfileRepo;

    @Autowired
    public UserProfileService(UserProfileRepo userProfileRepo) {
        this.userProfileRepo = userProfileRepo;
    }

    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepo.findAll();
    }

    public Optional<UserProfile> getUserProfileById(String id) {
        Optional<UserProfile> userProfile = Optional.empty();
        try {
            userProfile = userProfileRepo.findById(id);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return userProfile;
    }

    public Optional<UserProfile> getUserProfileByUserName(String username) {
        return userProfileRepo.findUserProfileByUsername(username);
    }

    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepo.save(userProfile);
    }

    public void deleteUserProfile(String id) {
        userProfileRepo.deleteById(id);
    }

    public UserProfile updateUserProfile(String id, UserProfile userProfile) {
        return userProfileRepo.findById(id)
                .map( uP -> {
                    uP.setTransactions(userProfile.getTransactions());
                    uP.setCategories(userProfile.getCategories());
                    uP.setBankAccounts(userProfile.getBankAccounts());
                    uP.setFirstName(userProfile.getFirstName());
                    uP.setLastName(userProfile.getLastName());
                    uP.setEmail(userProfile.getEmail());
                    return userProfileRepo.save(uP);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProfileRepo.findUserProfileByUsername(username).orElseThrow();
    }
}
