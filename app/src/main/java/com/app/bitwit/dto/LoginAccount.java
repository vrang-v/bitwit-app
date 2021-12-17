package com.app.bitwit.dto;

import com.app.bitwit.data.source.remote.dto.response.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class LoginAccount {
    
    Long id;
    
    String email;
    
    String name;
    
    boolean emailVerified;
    
    public LoginAccount(LoginResponse loginResponse) {
        this.id            = loginResponse.getId( );
        this.email         = loginResponse.getEmail( );
        this.name          = loginResponse.getName( );
        this.emailVerified = loginResponse.isEmailVerified( );
    }
}
