package com.toren.shewstringbe.dto.transactiondto;

import com.toren.shewstringbe.base.TransactionBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateTransactionDto extends TransactionBase {
    private Long id;
    private String userId;
    private Long bankAccountId;
    private Long repeatGroupId;
}
