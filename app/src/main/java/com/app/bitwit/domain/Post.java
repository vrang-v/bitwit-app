package com.app.bitwit.domain;

import android.annotation.SuppressLint;
import com.app.bitwit.util.TimeUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
public class Post {
    
    @SerializedName("postId")
    Long id;
    
    String title;
    
    String content;
    
    Account writer;
    
    int viewCount;
    
    int commentCount;
    
    int likeCount;
    
    boolean like;
    
    Instant createdAt;
    
    List<Comment> comments;
    
    Set<Stock> stocks;
    
    boolean edited;
    
    @SuppressLint("NewApi")
    public String getTimeString( ) {
        return TimeUtils.formatTimeString(createdAt.getEpochSecond( ));
    }
    
}
