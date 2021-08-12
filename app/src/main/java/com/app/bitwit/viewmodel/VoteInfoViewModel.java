package com.app.bitwit.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.CandlestickRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.util.Colors;
import com.github.mikephil.charting.data.Entry;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.app.bitwit.util.LiveDataUtils.observeUntilNotEmpty;

@Getter
@HiltViewModel
public class VoteInfoViewModel extends DisposableViewModel {
    
    private final VoteRepository        voteRepository;
    private final CandlestickRepository candlestickRepository;
    
    private final MutableLiveData<VoteItem>    voteItem = new MutableLiveData<>( );
    private final MutableLiveData<List<Entry>> entries  = new MutableLiveData<>( );
    
    private final MutableLiveData<SelectedCandlestick> selectedCandlestick = new MutableLiveData<>( );
    
    private final List<Candlestick> chartData = new ArrayList<>( );
    
    @Inject
    public VoteInfoViewModel(VoteRepository voteRepository, CandlestickRepository candlestickRepository) {
        this.voteRepository        = voteRepository;
        this.candlestickRepository = candlestickRepository;
    }
    
    public void loadVoteItem(long voteItemId) {
        voteRepository
                .loadVoteItem(voteItemId)
                .observeForever(voteItem::postValue);
    }
    
    public void loadCandlestick(String interval) {
        if (voteItem.getValue( ) == null) { return; }
        
        var ticker = voteItem.getValue( ).getTicker( );
        observeUntilNotEmpty(candlestickRepository.loadCandlesticks(ticker, interval, 300),
                candlesticks -> {
                    Collections.reverse(candlesticks);
                    chartData.clear( );
                    chartData.addAll(candlesticks);
                    var index = new AtomicInteger(0);
                    var entries = candlesticks
                            .stream( )
                            .map(candlestick -> new Entry(index.getAndAdd(1),
                                    (float)candlestick.getClosingPrice( ).doubleValue( )))
                            .collect(Collectors.toList( ));
                    this.entries.postValue(entries);
                }
        );
    }
    
    public void postSelectedCandlestick(int index) {
        if (chartData.isEmpty( )) { return; }
        
        this.selectedCandlestick.postValue(
                new SelectedCandlestick(
                        chartData.get(index),
                        chartData.get(0),
                        index == 0 ? chartData.get(0) : chartData.get(index - 1)
                )
        );
    }
    
    @SuppressLint("NewApi")
    public void refreshCandlestick(String interval) {
        if (voteItem.getValue( ) == null) { return; }
        
        var ticker = voteItem.getValue( ).getTicker( );
        addDisposable(
                candlestickRepository
                        .getLastDateTime(ticker, interval)
                        .filter(localDateTime -> {
                            switch (interval) {
                                case "1m":
                                    return isOldData(localDateTime, 1L, ChronoUnit.MINUTES);
                                case "3m":
                                    return isOldData(localDateTime, 3L, ChronoUnit.MINUTES);
                                case "5m":
                                    return isOldData(localDateTime, 5L, ChronoUnit.MINUTES);
                                case "10m":
                                    return isOldData(localDateTime, 10L, ChronoUnit.MINUTES);
                                case "30m":
                                    return isOldData(localDateTime, 30L, ChronoUnit.MINUTES);
                                case "1h":
                                    return isOldData(localDateTime, 1L, ChronoUnit.HOURS);
                                case "6h":
                                    return isOldData(localDateTime, 6L, ChronoUnit.HOURS);
                                case "12h":
                                    return isOldData(localDateTime, 12L, ChronoUnit.HOURS);
                                case "24h":
                                    return isOldData(localDateTime, 24L, ChronoUnit.HOURS);
                                default:
                                    return false;
                            }
                        })
                        .flatMapCompletable(localDateTime -> candlestickRepository.refreshCandlestick(ticker, interval))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(( ) -> { }, e -> Log.e("ERROR", "refreshCandlestick: ", e))
        );
    }
    
    @SuppressLint("NewApi")
    private boolean isOldData(LocalDateTime lastDataTime, Long amount, TemporalUnit temporalUnit) {
        var now          = LocalDateTime.now( );
        var nextDataTime = lastDataTime.plus(amount, temporalUnit);
        return now.isAfter(nextDataTime) || now.isEqual(nextDataTime);
    }
    
    @Data
    @AllArgsConstructor
    public static class SelectedCandlestick {
        
        Candlestick selected;
        Candlestick first;
        Candlestick previous;
        
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
            if (minute != 0) { stringBuilder.append(String.format("%s분", minute)); }
            if (second != 0) { stringBuilder.append(String.format("%s초", minute)); }
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
}
