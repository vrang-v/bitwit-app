package com.app.bitwit.data.source.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity(primaryKeys = {"ticker", "dataTime", "interval"})
public class Candlestick {
    
    @NonNull
    String ticker;
    
    @NonNull
    String dataTime;
    
    @NonNull
    String interval;
    
    Double openPrice;
    
    Double closingPrice;
    
    Double highPrice;
    
    Double lowPrice;
    
    Double tradingVolume;
    
}
