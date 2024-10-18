package com.toren.shewstringbe;

import com.toren.shewstringbe.config.JwtFilter;
import com.toren.shewstringbe.enums.AccountType;
import com.toren.shewstringbe.enums.TransactionType;
import com.toren.shewstringbe.models.BankAccount;
import com.toren.shewstringbe.models.Transaction;
import com.toren.shewstringbe.models.UserProfile;
import com.toren.shewstringbe.repository.BankAccountRepo;
import com.toren.shewstringbe.repository.TransactionRepo;
import com.toren.shewstringbe.repository.UserProfileRepo;
import com.toren.shewstringbe.service.BankAccountService;
import com.toren.shewstringbe.service.JwtService;
import com.toren.shewstringbe.service.TransactionService;
import com.toren.shewstringbe.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = ShewstringBeApplication.class)
@ActiveProfiles("test")
@DisplayName("ShewString Tests")
class ShewstringBeApplicationTests {
    

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserProfileRepo userProfileRepo;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BankAccountRepo bankAccountRepo;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepo transactionRepo;
    
    private UserProfile createdUserProfile;
    private BankAccount createdBankAccount;
    private Transaction createdTransaction;
    private int count = 0;
    
    @MockBean
    private JwtFilter jwtFilter;
    @MockBean
    private JwtService jwtService;
    
    @BeforeEach
    public void setUpDbInfo() {
        count++;
        createdUserProfile = userProfileService.createUserProfile(newUpUserProfile());

        BankAccount newBankAccount = newUpBankAccount();
        newBankAccount.setUserProfile(createdUserProfile);

        createdBankAccount = bankAccountService.createBankAccount(newBankAccount);

        Transaction newTransaction = newUpTransaction();
        newTransaction.setUserProfile(createdUserProfile);
        newTransaction.setBankAccount(createdBankAccount);

        createdTransaction = transactionService.createTransaction(newTransaction);
    }
    
    @AfterEach
    public void clearDbInfo() {
        transactionRepo.deleteAll();
        bankAccountRepo.deleteAll();
        userProfileRepo.deleteAll();
    }
    
    @Test
    @Transactional
    @DisplayName("User Service Update Method")
    public void getProfileByUsername() {
        UserProfile newUser = newUpUserProfile();
        newUser.setEmail("different@email.com");
        newUser.setId(createdUserProfile.getId());
        
        UserProfile userProfile = userProfileService.updateUserProfile(createdUserProfile.getId(),newUser);
        
        assertEquals(userProfile.getId(), createdUserProfile.getId());
        assertNotEquals(userProfile.getEmail(),newUpUserProfile().getEmail());
    }
    
    @Test
    @Transactional
    @DisplayName("Bank Account Service Get Method")
    public void bankAccountService(){
        BankAccount bankAccount = bankAccountService.getBankAccountById(createdBankAccount.getId()).orElseThrow();
        
        assertEquals(bankAccount.toString(),createdBankAccount.toString());
    }
    
    @Test
    @Transactional
    @DisplayName("Transaction Service Delete Method")
    public void transactionService(){
        transactionService.deleteTransaction(createdTransaction.getId());
        
        Optional<Transaction> t = transactionService.getTransactionById(createdTransaction.getId());
        
        assert(t.isEmpty());
    }
    
    @ParameterizedTest
    @Transactional
    @MethodSource("userProfileStream")
    @DisplayName("User Profile Repo Tests")
    public void userProfileRepo(UserProfile userProfile) {
        UserProfile user = userProfileRepo.save(userProfile);
        
        assertFalse(user.getId().isEmpty());
        assertFalse(user.getId().isBlank());
        
        Optional<UserProfile> fromUsername = userProfileRepo.findUserProfileByUsername(userProfile.getUsername());
        
        assert(fromUsername.isPresent());
        
        Optional<UserProfile> fromId = userProfileRepo.findById(userProfile.getId());
        
        assert(fromId.isPresent());
        
        userProfileRepo.deleteById(userProfile.getId());
        
        Optional<UserProfile> afterDelete = userProfileRepo.findById(userProfile.getId());
        
        assertFalse(afterDelete.isPresent());
    }
    
    @ParameterizedTest
    @Transactional
    @MethodSource("bankAccountStream")
    @DisplayName("Bank Account Repo Tests")
    public void bankAccountRepo(BankAccount bankAccount) {
        bankAccount.setUserProfile(createdUserProfile);
        BankAccount account = bankAccountRepo.save(bankAccount);
        
        Optional<BankAccount> fromId = bankAccountRepo.findById(account.getId());
        
        assert(fromId.isPresent());
        
        bankAccountRepo.deleteById(account.getId());
        
        Optional<BankAccount> afterDelete = bankAccountRepo.findById(account.getId());
        
        assertFalse(afterDelete.isPresent());
    }
    
    @ParameterizedTest
    @Transactional
    @MethodSource("transactionStream")
    @DisplayName("Transaction Repo Tests")
    public void transactionRepo(Transaction transaction) {
        transaction.setBankAccount(createdBankAccount);
        transaction.setUserProfile(createdUserProfile);
        Transaction user = transactionRepo.save(transaction);
        
        Optional<Transaction> fromId = transactionRepo.findById(transaction.getId());
        
        assert(fromId.isPresent());
        
        transactionRepo.deleteById(transaction.getId());
        
        Optional<Transaction> afterDelete = transactionRepo.findById(transaction.getId());
        
        assertFalse(afterDelete.isPresent());
    }
    
    public static UserProfile newUpUserProfile(){
        UserProfile userProfile = new UserProfile();
        userProfile.setPassword("password");
        userProfile.setTransactions(new ArrayList<>());
        userProfile.setCategories(new ArrayList<>(List.of("None")));
        userProfile.setLastName("LastName");
        userProfile.setFirstName("First");
        userProfile.setBankAccounts(new ArrayList<>());
        userProfile.setEmail("email@email.com");
        userProfile.setUsername("Username");
        
        return userProfile;
    }
    
    public static BankAccount newUpBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountType(AccountType.Checking);
        bankAccount.setTitle("Test Account");
        bankAccount.setTransactions(new ArrayList<>());
        
        return bankAccount;
    }
    
    public static Transaction newUpTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("55.65"));
        transaction.setTitle("Test Transaction");
        transaction.setDate(LocalDate.of(2024,10,15));
        transaction.setCategory("None");
        transaction.setTransactionType(TransactionType.Credit);
        transaction.setDescription("");
        transaction.setCreatedOn(ZonedDateTime.now());
        
        return transaction;
    }
    
    public static Stream<UserProfile> userProfileStream() {
        UserProfile userProfile1 = new UserProfile();
        UserProfile userProfile2 = new UserProfile();
        UserProfile userProfile3 = new UserProfile();
        
        userProfile1.setEmail("user1@email.com");
        userProfile1.setUsername("User1");
        userProfile1.setFirstName("User1First");
        userProfile1.setLastName("User1Last");
        userProfile1.setPassword("User1Pass");
        
        userProfile2.setEmail("user2@email.com");
        userProfile2.setUsername("User2");
        userProfile2.setFirstName("User2First");
        userProfile2.setLastName("User2Last");
        userProfile2.setPassword("User2Pass");
        
        userProfile3.setEmail("user3@email.com");
        userProfile3.setUsername("User3");
        userProfile3.setFirstName("User3First");
        userProfile3.setLastName("User3Last");
        userProfile3.setPassword("User3Pass");
        
        return Stream.of(userProfile1, userProfile2, userProfile3);
    }
    
    public static Stream<BankAccount> bankAccountStream() {
        BankAccount bankAccount1 = new BankAccount();
        BankAccount bankAccount2 = new BankAccount();
        BankAccount bankAccount3 = new BankAccount();
        
        bankAccount1.setAccountType(AccountType.Checking);
        bankAccount1.setTitle("BankAccount1");
        
        bankAccount2.setAccountType(AccountType.Checking);
        bankAccount2.setTitle("BankAccount2");
        
        bankAccount3.setAccountType(AccountType.Checking);
        bankAccount3.setTitle("BankAccount3");
        
        return Stream.of(bankAccount1, bankAccount2, bankAccount3);
    }
    
    public static Stream<Transaction> transactionStream() {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        Transaction transaction3 = new Transaction();
        
        transaction1.setTransactionType(TransactionType.Credit);
        transaction1.setDate(LocalDate.of(2024,9,7));
        transaction1.setAmount(new BigDecimal("35.00"));
        transaction1.setTitle("Transaction1");
        transaction1.setCategory("None");
        transaction1.setDescription("");
        
        
        transaction2.setTransactionType(TransactionType.Credit);
        transaction2.setDate(LocalDate.of(2024,8,8));
        transaction2.setAmount(new BigDecimal("68.35"));
        transaction2.setTitle("Transaction2");
        transaction2.setCategory("None");
        transaction2.setDescription("");
        
        transaction3.setTransactionType(TransactionType.Credit);
        transaction3.setDate(LocalDate.of(2024,10,15));
        transaction3.setAmount(new BigDecimal("75.04"));
        transaction3.setTitle("Transaction3");
        transaction3.setCategory("None");
        transaction3.setDescription("");
        
        return Stream.of(transaction1, transaction2, transaction3);
    }
}