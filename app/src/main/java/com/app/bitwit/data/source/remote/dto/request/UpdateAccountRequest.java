package com.app.bitwit.data.source.remote.dto.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UpdateAccountRequest {
    
    @SerializedName("name")
    private String nickname;
    
    private String email;
    
    private String password;
}
