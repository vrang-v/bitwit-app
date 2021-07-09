package com.app.bitwit.domain;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Stock
{
    Long id;
    
    String name;
    
    String createdAt;
    
    String updatedAt;
}
