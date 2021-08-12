package com.app.bitwit.domain;

import lombok.Data;

@Data
public class Candlestick_ {
    
    String dataTime;
    
    Double openPrice;
    
    Double closingPrice;
    
    Double highPrice;
    
    Double lowPrice;
    
    Double tradingVolume;
}
