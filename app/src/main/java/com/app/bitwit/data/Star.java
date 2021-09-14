package com.app.bitwit.data;

import com.app.bitwit.domain.Account;
import com.app.bitwit.domain.Post;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Star {
    
    @SerializedName("starId")
    Long id;
    
    Account account;
    
    Post post;
    
}
