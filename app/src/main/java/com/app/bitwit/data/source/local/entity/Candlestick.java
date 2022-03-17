package com.app.bitwit.data.source.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.Locale;

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
    
    public Double getFluctuateRate( ) {
        if (openPrice == 0) {
            return 0.00;
        }
        return (closingPrice - openPrice) / openPrice * 100;
    }
    
    public String getFluctuateRateString( ) {
        return String.format(Locale.getDefault( ), getFluctuateRate( ) > 0 ? "+%.2f%%" : "%.2f%%", getFluctuateRate( ));
    }
    
    public String getOpenPriceString( ) {
        if (openPrice < 100) {
            return String.format(Locale.getDefault( ), "%.2f원", openPrice);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(openPrice) + "원";
    }
    
    public String getClosingPriceString( ) {
        if (closingPrice < 100) {
            return String.format(Locale.getDefault( ), "%.2f원", closingPrice);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(closingPrice) + "원";
    }
    
    public String getHighPriceString( ) {
        if (highPrice < 100) {
            return String.format(Locale.getDefault( ), "%.2f원", highPrice);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(highPrice) + "원";
    }
    
    public String getLowPriceString( ) {
        if (lowPrice < 100) {
            return String.format(Locale.getDefault( ), "%.2f원", lowPrice);
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(lowPrice) + "원";
    }
    
    public boolean isUp( ) {
        return closingPrice > openPrice;
    }
    
}
