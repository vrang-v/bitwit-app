package com.app.bitwit.data.source.remote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest
{
    private String name;
    
    private String email;
    
    private String password;
}
