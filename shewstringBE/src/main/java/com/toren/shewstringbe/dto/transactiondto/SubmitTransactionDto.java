package com.toren.shewstringbe.dto.transactiondto;

import com.toren.shewstringbe.base.TransactionBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubmitTransactionDto extends TransactionBase {
    private String userId;
    private Long bankAccountId;
}
