package com.app.bitwit.domain;

import lombok.Data;

@Data
public class Candlestick {
    
    String dataTime;
    
    Double openPrice;
    
    Double closingPrice;
    
    Double highPrice;
    
    Double lowPrice;
    
    Double tradingVolume;
}
