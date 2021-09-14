package com.app.bitwit.data.source.remote.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchStockRequest {
    
    private String keyword;
    
}
