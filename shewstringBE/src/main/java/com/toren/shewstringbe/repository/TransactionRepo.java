package com.toren.shewstringbe.repository;

import com.toren.shewstringbe.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> getTransactionsByBankAccount_Id(Long id);
}
