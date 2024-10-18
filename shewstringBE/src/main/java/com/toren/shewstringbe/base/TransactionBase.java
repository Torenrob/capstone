package com.toren.shewstringbe.base;

import com.toren.shewstringbe.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public abstract class TransactionBase {
    //No id in base - the creation dto will not have id
    private String title;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String description;
}
