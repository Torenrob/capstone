package com.toren.shewstringbe.service;

import com.toren.shewstringbe.models.Transaction;
import com.toren.shewstringbe.repository.TransactionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepo transactionRepo;

    @Autowired
    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepo.findById(id);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepo.save(transaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepo.findById(id).orElseThrow();
        log.info("Deleting Transaction");
        transaction.setUserProfile(null);
        transaction.setBankAccount(null);
        transactionRepo.save(transaction);

        transactionRepo.delete(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction transaction) {
        return transactionRepo.findById(id)
                .map( t -> {
                    t.setTitle(transaction.getTitle());
                    t.setUserProfile(transaction.getUserProfile());
                    t.setDate(transaction.getDate());
                    t.setAmount(transaction.getAmount());
                    t.setCategory(transaction.getCategory());
                    t.setTransactionType(transaction.getTransactionType());
                    t.setDescription(transaction.getDescription());
                    t.setBankAccount(transaction.getBankAccount());
                    return transactionRepo.save(t);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
