package com.toren.shewstringbe.dto.bankaccountdto;

import com.toren.shewstringbe.base.BankAccountBase;
import com.toren.shewstringbe.enums.AccountType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubmitBankAccountDto extends BankAccountBase {
    private String title;
    private AccountType accountType;
    private String userId;

    @Override
    public Object getTransactions() {
        return null;
    }
}
