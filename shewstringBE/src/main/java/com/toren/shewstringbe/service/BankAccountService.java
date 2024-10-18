package com.toren.shewstringbe.service;

import com.toren.shewstringbe.models.BankAccount;
import com.toren.shewstringbe.models.Transaction;
import com.toren.shewstringbe.models.UserProfile;
import com.toren.shewstringbe.repository.BankAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepo bankAccountRepo;
    private final UserProfileService userProfileService;
    private final TransactionService transactionService;

    @Autowired
    public BankAccountService(BankAccountRepo bankAccountRepo, UserProfileService userProfileService,
                            TransactionService transactionService) {
        this.bankAccountRepo = bankAccountRepo;
        this.userProfileService = userProfileService;
        this.transactionService = transactionService;
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepo.findAll();
    }

    public Optional<BankAccount> getBankAccountById(Long id) {
        return bankAccountRepo.findById(id);
    }

    public BankAccount createBankAccount(BankAccount bankAccount) {
        return bankAccountRepo.save(bankAccount);
    }

    public List<BankAccount> getBankAccountsByUserId(String userId) {
        UserProfile userProfile = userProfileService.getUserProfileById(userId).orElseThrow();

        return bankAccountRepo.getBankAccountsByUserProfile(userProfile);
    }

    public void deleteBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepo.getReferenceById(id);

        for (Transaction t: bankAccount.getTransactions()) {
            transactionService.deleteTransaction(t.getId());
        }


        bankAccount.setUserProfile(null);
        bankAccountRepo.save(bankAccount);

        bankAccountRepo.delete(bankAccount);
    }

    public BankAccount updateBankAccount(Long id, BankAccount bankAccount) {
        return bankAccountRepo.findById(id)
                .map( bA -> {
                    bA.setTitle(bankAccount.getTitle());
                    bA.setAccountType(bankAccount.getAccountType());
                    bA.setTransactions(bankAccount.getTransactions());
                    bA.setUserProfile(bankAccount.getUserProfile());
                    return bankAccountRepo.save(bA);
                })
                .orElseThrow(() -> new RuntimeException("Bank Account not found"));
    }
}
