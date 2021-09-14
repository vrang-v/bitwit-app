package com.app.bitwit.data.source.remote.dto.response;

import lombok.Data;

@Data
public class DuplicateCheckResponse {
    
    String email;
    
    boolean duplicate;
}
