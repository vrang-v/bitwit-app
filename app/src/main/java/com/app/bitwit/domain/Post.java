package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Post {
    
    @SerializedName("postId")
    Long id;
    
    String title;
    
    String contents;
    
    Account writer;
    
    int viewCount;
    
    int starCount;
    
    boolean star;
}
