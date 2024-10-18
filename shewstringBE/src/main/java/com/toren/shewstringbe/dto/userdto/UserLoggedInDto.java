package com.toren.shewstringbe.dto.userdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toren.shewstringbe.base.UserProfileBase;
import com.toren.shewstringbe.config.ApplicationConfig;
import com.toren.shewstringbe.models.BankAccount;
import com.toren.shewstringbe.models.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoggedInDto extends UserProfileBase {
    private String id;
    private String token;

    @Override
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ApplicationConfig.ExcludeNull.class)
    public List<Transaction> getTransactions() {
        return null;
    }

    @Override
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ApplicationConfig.ExcludeNull.class)
    public List<BankAccount> getBankAccounts() {
        return null;
    }
}
