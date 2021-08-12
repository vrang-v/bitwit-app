package com.app.bitwit.domain;

import lombok.Data;

import java.util.List;

@Data
public class Stock {
    
    Long id;
    
    String ticker;
    
    String fullName;
    
    String koreanName;
    
    Double currentPrice;
    
    Double realTimeFluctuation;
    
    Double fluctuate24h;
    
    Double fluctuateRate24h;
    
    Double marketCap;
    
    List<DailyInfo> dailyInfo;
    
    String createdAt;
    
    String updatedAt;
}
