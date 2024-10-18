package com.toren.shewstringbe.controller;

import com.toren.shewstringbe.dto.transactiondto.ReturnTransactionDto;
import com.toren.shewstringbe.dto.transactiondto.SubmitTransactionDto;
import com.toren.shewstringbe.dto.transactiondto.UpdateTransactionDto;
import com.toren.shewstringbe.mapper.TransactionMapper;
import com.toren.shewstringbe.models.Transaction;
import com.toren.shewstringbe.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping
    public ResponseEntity<ReturnTransactionDto> postNewTransaction(@RequestBody SubmitTransactionDto transaction) {
        log.info("Trying to create new transaction");
        Transaction tr = transactionService.createTransaction(
            transactionMapper.fromSubmitTransactionToTransaction(transaction));

        log.info("New Transaction created");
        return ResponseEntity.ok(transactionMapper.fromTransactionToReturnTransactionDto(tr));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnTransactionDto> updateTransactionById(@PathVariable Long id,
                                                @RequestBody UpdateTransactionDto updateTransactionDto) {
        Transaction tr = transactionService.updateTransaction(id,
            transactionMapper.fromUpdateTransactionDtoToTransaction(updateTransactionDto));
        ReturnTransactionDto returnTransactionDto = transactionMapper.fromTransactionToReturnTransactionDto(tr);
        return ResponseEntity.ok(returnTransactionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
