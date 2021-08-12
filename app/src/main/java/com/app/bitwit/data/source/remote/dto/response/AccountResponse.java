package com.app.bitwit.data.source.remote.dto.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AccountResponse {
    
    @SerializedName("id")
    Long id;
    
    @SerializedName("name")
    String name;
    
    @SerializedName("email")
    String email;
}
