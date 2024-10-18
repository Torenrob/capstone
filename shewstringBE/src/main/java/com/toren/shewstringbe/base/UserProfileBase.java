package com.toren.shewstringbe.base;

import lombok.Data;

@Data
public abstract class UserProfileBase {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Object transactions;
    private Object categories;
    private Object bankAccounts;
}
