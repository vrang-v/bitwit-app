package com.app.bitwit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.var;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter @FieldDefaults(level = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Account
{
    Long id;
    
    String name;
    
    String email;
    
    String password;
    
    public static Account createAccount(String name, String email, String password)
    {
        Account account = new Account( );
        account.name     = name;
        account.email    = email;
        account.password = password;
        return account;
    }
}
