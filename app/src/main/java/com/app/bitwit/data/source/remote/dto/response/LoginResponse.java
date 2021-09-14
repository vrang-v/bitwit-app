package com.app.bitwit.data.source.remote.dto.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class LoginResponse {
    
    String jwt;
    
    @SerializedName("accountId")
    Long id;
    
    String email;
    
    String name;
}
