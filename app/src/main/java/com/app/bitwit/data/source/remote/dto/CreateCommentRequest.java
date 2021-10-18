package com.app.bitwit.data.source.remote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    
    String content;
    
    Long postId;
    
    Long parentId;
    
    public CreateCommentRequest(String content, Long postId) {
        this.content = content;
        this.postId  = postId;
    }
}
