package com.app.bitwit.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyInfo {
    
    LocalDate infoDate;
    
    Double openingPrice;
    
    Double closingPrice;
    
    Double minPrice;
    
    Double maxPrice;
    
    Double tradingVolume;
    
}
