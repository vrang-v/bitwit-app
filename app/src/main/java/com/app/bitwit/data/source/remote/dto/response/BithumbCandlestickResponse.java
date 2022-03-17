package com.app.bitwit.data.source.remote.dto.response;

import android.annotation.SuppressLint;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BithumbCandlestickResponse {
    
    String status;
    
    @SerializedName("data")
    Object[][] rawCandlesticks;
    
    @SuppressLint("NewApi")
    public List<Candlestick> getCandlesticks(String ticker, String interval) {
        return Arrays.stream(rawCandlesticks)
                     .map(candlestick -> Candlestick
                             .builder( )
                             .ticker(ticker)
                             .interval(interval)
                             .dataTime(Instant.ofEpochMilli((long)(double)candlestick[0]).toString( ))
                             .openPrice(Double.parseDouble((String)candlestick[1]))
                             .closingPrice(Double.parseDouble((String)candlestick[2]))
                             .highPrice(Double.parseDouble((String)candlestick[3]))
                             .lowPrice(Double.parseDouble((String)candlestick[4]))
                             .tradingVolume(Double.parseDouble((String)candlestick[5]))
                             .build( )
                     )
                     .collect(Collectors.toList( ));
    }
}
