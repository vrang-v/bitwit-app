package com.app.bitwit.domain;

import android.annotation.SuppressLint;
import com.app.bitwit.data.source.remote.dto.response.AccountResponse;
import com.app.bitwit.util.TimeUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Comment {
    
    @SerializedName("commentId")
    Long id;
    
    String content;
    
    AccountResponse writer;
    
    int depth;
    
    boolean editable;
    
    boolean edited;
    
    boolean deleted;
    
    int likeCount;
    
    boolean like;
    
    Instant createdAt;
    
    Long parentId;
    
    List<Comment> children;
    
    @SuppressLint("NewApi")
    public String getTimeString( ) {
        return TimeUtils.formatTimeString(createdAt.getEpochSecond( ));
    }
    
    public void setEditable(Long accountId) {
        this.editable = writer != null && writer.getId( ).equals(accountId);
    }
}
