package com.toren.shewstringbe.controller;

import com.toren.shewstringbe.dto.bankaccountdto.ReturnBankAccountDto;
import com.toren.shewstringbe.dto.bankaccountdto.SubmitBankAccountDto;
import com.toren.shewstringbe.mapper.BankAccountMapper;
import com.toren.shewstringbe.models.BankAccount;
import com.toren.shewstringbe.service.BankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bankAccounts")
public class BankAccountController {

    private static final Logger log = LoggerFactory.getLogger(BankAccountController.class);
    private final BankAccountService bankAccountService;
    private final BankAccountMapper bankAccountMapper;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, BankAccountMapper bankAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.bankAccountMapper = bankAccountMapper;
    }

    @GetMapping
    public ResponseEntity<List<ReturnBankAccountDto>> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();

        List<ReturnBankAccountDto> returnBankAccountDtoList = bankAccounts.stream()
            .map(bankAccountMapper::fromBankAccountToReturnBankAccountDto).toList();

        return ResponseEntity.ok(returnBankAccountDtoList);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReturnBankAccountDto>> getBankAccountByUserId(@PathVariable String userId) {
        log.info("Activated");
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUserId(userId);

        List<ReturnBankAccountDto> returnBankAccountDtoList = bankAccounts.stream()
            .map(bankAccountMapper::fromBankAccountToReturnBankAccountDto).toList();

        return ResponseEntity.ok(returnBankAccountDtoList);
    }

    @PostMapping
    public ResponseEntity<ReturnBankAccountDto> createBankAccount(
        @RequestBody SubmitBankAccountDto submitBankAccountDto) {

        BankAccount bankAccount = bankAccountService.createBankAccount(
            bankAccountMapper.fromSubmitBankAccountDtoToBankAccount(submitBankAccountDto));

        log.info("Bank Account created");
        ReturnBankAccountDto returnBankAccountDto = bankAccountMapper.fromBankAccountToReturnBankAccountDto(bankAccount);
        log.info("Return Dto created");
        return ResponseEntity.ok(returnBankAccountDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBankAccountById(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);

        return ResponseEntity.ok("Account deleted");
    }
}
