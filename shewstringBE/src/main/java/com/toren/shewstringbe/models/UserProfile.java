package com.toren.shewstringbe.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.toren.shewstringbe.base.UserProfileBase;
import com.toren.shewstringbe.models.converter.StringListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class UserProfile extends UserProfileBase implements UserDetails {
    @Id
    @UuidGenerator
    private String id;

    @Column(unique = true)
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "User first name is required")
    private String firstName;

    @NotBlank(message = "User last name is required")
    private String lastName;

    @Column(unique = true)
    @NotBlank(message = "User email is required")
    private String email;

    @NotBlank(message = "User password is required")
    private String password;

    private ZonedDateTime createdOn = ZonedDateTime.now(ZoneOffset.UTC);

    @Convert(converter = StringListConverter.class)
    private List<String> categories = new ArrayList<>(List.of("None"));

    @JsonManagedReference("user_transactions")
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

    @JsonManagedReference("user_accounts")
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BankAccount> bankAccounts = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


