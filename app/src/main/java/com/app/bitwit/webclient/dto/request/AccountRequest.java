package com.app.bitwit.webclient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest
{
    private String name;
    
    private String email;
    
    private String password;
}
