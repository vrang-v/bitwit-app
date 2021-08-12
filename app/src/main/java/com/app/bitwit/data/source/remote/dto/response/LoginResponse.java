package com.app.bitwit.data.source.remote.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    
    private String jwt;
    
    private Long id;
    
    private String email;
    
    private String name;
}
