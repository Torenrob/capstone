package com.toren.shewstringbe.repository;

import com.toren.shewstringbe.models.BankAccount;
import com.toren.shewstringbe.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    List<BankAccount> getBankAccountsByUserProfile(UserProfile userProfile);
}
