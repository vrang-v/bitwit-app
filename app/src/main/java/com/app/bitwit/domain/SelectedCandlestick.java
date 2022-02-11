package com.app.bitwit.domain;

import android.annotation.SuppressLint;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.app.bitwit.constant.Colors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
@AllArgsConstructor
public class SelectedCandlestick {
    
    private Candlestick selected;
    private Candlestick first;
    private Candlestick previous;
    
    @SuppressLint("NewApi")
    public String getYearMonthDay( ) {
        var instant  = Instant.parse(selected.getDataTime( ));
        var dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault( ));
        var year     = dateTime.getYear( );
        var month    = dateTime.getMonthValue( );
        var day      = dateTime.getDayOfMonth( );
        return String.format("%s년 %s월 %s일", year, month, day);
    }
    
    @SuppressLint("NewApi")
    public String getHourMinuteSecond( ) {
        var instant  = Instant.parse(selected.getDataTime( ));
        var dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault( ));
        var hour     = dateTime.getHour( );
        var minute   = dateTime.getMinute( );
        var second   = dateTime.getSecond( );
        
        var stringBuilder = new StringBuilder( );
        if (hour == 0 && minute == 0) {
            stringBuilder.append(dateTime.getDayOfWeek( ).getDisplayName(TextStyle.FULL, Locale.KOREA));
            return stringBuilder.toString( );
        }
        stringBuilder.append(String.format("%s시", hour));
        if (minute != 0) { stringBuilder.append(String.format(" %s분", minute)); }
        if (second != 0) { stringBuilder.append(String.format(" %s초", minute)); }
        return stringBuilder.toString( );
    }
    
    public String getPriceString( ) {
        if (selected.getClosingPrice( ) < 100) {
            return String.format(Locale.getDefault( ), "%.2f 원", selected.getClosingPrice( ));
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(selected.getClosingPrice( )) + " 원";
    }
    
    public String getFluctuateRateString( ) {
        return makeStringFormat(getFluctuateRate( ));
    }
    
    public String getWholeFluctuateRateString( ) {
        return makeStringFormat(getWholeFluctuateRate( ));
    }
    
    public int getFluctuateRateColor( ) {
        return getColor(getFluctuateRate( ));
    }
    
    public int getWholeFluctuateRateColor( ) {
        return getColor(getWholeFluctuateRate( ));
    }
    
    private String makeStringFormat(double fluctuateRate) {
        if (fluctuateRate > 0) {
            return String.format(Locale.getDefault( ), "+%.2f%%", fluctuateRate);
        }
        else if (fluctuateRate < 0) {
            return String.format(Locale.getDefault( ), "%.2f%%", fluctuateRate);
        }
        else {
            return "0.00%";
        }
    }
    
    private int getColor(double fluctuateRate) {
        if (fluctuateRate > 0) {
            return Colors.PRIMARY_RED;
        }
        else if (fluctuateRate < 0) {
            return Colors.PRIMARY_BLUE;
        }
        else {
            return Colors.GRAY;
        }
    }
    
    private double getFluctuateRate( ) {
        return (selected.getClosingPrice( ) - previous.getClosingPrice( )) / previous.getClosingPrice( ) * 100;
    }
    
    private double getWholeFluctuateRate( ) {
        return (selected.getClosingPrice( ) - first.getClosingPrice( )) / first.getClosingPrice( ) * 100;
    }
}
