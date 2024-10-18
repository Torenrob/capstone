package com.toren.shewstringbe.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.toren.shewstringbe.base.BankAccountBase;
import com.toren.shewstringbe.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class BankAccount extends BankAccountBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bank Account title is required")
    private String title;

    @NotBlank(message = "Bank Account type is required")
    private AccountType accountType;

    @JsonManagedReference("account_transactions")
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @JsonBackReference("user_accounts")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", accountType=" + accountType +
            ", transactions=" + transactions +
        '}';
    }
}
