package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Stock {
    
    @SerializedName("stockId")
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
