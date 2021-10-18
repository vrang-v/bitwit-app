package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Like {
    
    @SerializedName("likeId")
    Long id;
    
    Account account;
    
    Post post;
    
    boolean like;
    
    Long accountId;
    
}
