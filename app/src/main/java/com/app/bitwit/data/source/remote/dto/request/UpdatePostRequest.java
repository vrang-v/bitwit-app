package com.app.bitwit.data.source.remote.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePostRequest {
    
    Long postId;
    
    String title;
    
    String content;
    
    List<String> tickers;
    
}