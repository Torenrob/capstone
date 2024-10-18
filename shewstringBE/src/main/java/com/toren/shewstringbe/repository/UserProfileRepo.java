package com.toren.shewstringbe.repository;

import com.toren.shewstringbe.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepo extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findUserProfileByUsername(String username);
}
