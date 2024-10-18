package com.toren.shewstringbe.dto.bankaccountdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toren.shewstringbe.base.BankAccountBase;
import com.toren.shewstringbe.config.ApplicationConfig;
import com.toren.shewstringbe.mapper.TransactionMapper;
import com.toren.shewstringbe.models.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class ReturnBankAccountDto extends BankAccountBase {
    private Long id;
    private List<Object> repeatGroups;
    private List<Transaction> transactions;
    private final TransactionMapper transactionMapper;

    @Autowired
    public ReturnBankAccountDto(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TreeMap<String, List<Transaction>> getTransactions() {
        return transactionMapper.fromListToSortedMap(transactions);
    }

    @SuppressWarnings("SameReturnValue")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ApplicationConfig.ExcludeNull.class)
    public ModelMapper getModelMapper() {
        return null;
    }
}
