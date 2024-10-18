package com.toren.shewstringbe.mapper;

import com.toren.shewstringbe.dto.transactiondto.ReturnTransactionDto;
import com.toren.shewstringbe.dto.transactiondto.SubmitTransactionDto;
import com.toren.shewstringbe.dto.transactiondto.UpdateTransactionDto;
import com.toren.shewstringbe.models.Transaction;
import com.toren.shewstringbe.service.BankAccountService;
import com.toren.shewstringbe.service.TransactionService;
import com.toren.shewstringbe.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Component
public class TransactionMapper {

    private final BankAccountService bankAccountService;
    private final UserProfileService userProfileService;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;

    @Autowired
    public TransactionMapper(BankAccountService bankAccountService, UserProfileService userProfileService,
                             ModelMapper modelMapper, TransactionService transactionService) {
        this.bankAccountService = bankAccountService;
        this.userProfileService = userProfileService;
        this.modelMapper = modelMapper;
        this.transactionService = transactionService;
    }

    public TreeMap<String, List<Transaction>> fromListToSortedMap(List<Transaction> transactionList) {
        TreeMap<String,List<Transaction>> transactionTreeMap = new TreeMap<>();

        if(transactionList == null) {
            return transactionTreeMap;
        }

        for (Transaction t: transactionList) {
            String date = t.getDate().toString();

            if (transactionTreeMap.containsKey(date)) {
                transactionTreeMap.get(date).add(t);
            } else {
                transactionTreeMap.put(date, new ArrayList<>(List.of(t)));
            }
        }

        return transactionTreeMap;
    }

    public Transaction fromSubmitTransactionToTransaction(SubmitTransactionDto submitTransactionDto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(submitTransactionDto.getTransactionType());
        transaction.setTitle(submitTransactionDto.getTitle());
        transaction.setAmount(submitTransactionDto.getAmount());
        transaction.setDate(submitTransactionDto.getDate());
        transaction.setCategory(submitTransactionDto.getCategory());
        transaction.setBankAccount(bankAccountService
            .getBankAccountById(submitTransactionDto.getBankAccountId()).orElseThrow());
        transaction.setUserProfile(userProfileService
            .getUserProfileById(submitTransactionDto.getUserId()).orElseThrow());

        return transaction;
    }

    public ReturnTransactionDto fromTransactionToReturnTransactionDto(Transaction transaction) {
        ReturnTransactionDto returnTransactionDto = modelMapper.map(transaction, ReturnTransactionDto.class);

        returnTransactionDto.setBankAccountId(transaction.getBankAccount().getId());

        return returnTransactionDto;
    }

    public Transaction fromUpdateTransactionDtoToTransaction(UpdateTransactionDto updateTransactionDto) {
        Transaction updatedTransaction = modelMapper.map(updateTransactionDto, Transaction.class);

        Transaction originalTransaction = transactionService.getTransactionById(updatedTransaction.getId()).orElseThrow();

        updatedTransaction.setBankAccount(originalTransaction.getBankAccount());
        updatedTransaction.setUserProfile(originalTransaction.getUserProfile());

        return updatedTransaction;
    }
}
