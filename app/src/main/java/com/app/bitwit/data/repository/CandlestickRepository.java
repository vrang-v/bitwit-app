package com.app.bitwit.data.repository;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import com.app.bitwit.data.source.local.CandlestickDao;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.app.bitwit.data.source.remote.BithumbServiceClient;
import com.app.bitwit.data.source.remote.dto.response.BithumbCandlestickResponse;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.var;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CandlestickRepository {
    
    private final BithumbServiceClient bithumbServiceClient;
    private final CandlestickDao       candlestickDao;
    
    public Single<BithumbCandlestickResponse> getCandlestick(String ticker, String interval) {
        return bithumbServiceClient
                .getCandlestick(ticker, interval)
                .map(HttpUtils::get2xxBody);
    }
    
    public Completable refreshCandlestick(String ticker, String interval) {
        return getCandlestick(ticker, interval)
                .map(response -> response.getCandlesticks(ticker, interval))
                .flatMapCompletable(candlestickDao::insertCandlesticks);
    }
    
    public LiveData<List<Candlestick>> loadCandlesticks(String ticker, String interval, int size) {
        return candlestickDao
                .loadByTickerAndInterval(ticker, interval, size);
    }
    
    @SuppressLint("NewApi")
    public Single<LocalDateTime> getLastDateTime(String ticker, String interval) {
        return candlestickDao
                .getLastDataTime(ticker, interval)
                .map(timeString -> {
                    var instant = Instant.parse(timeString);
                    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault( ));
                })
                .defaultIfEmpty(LocalDateTime.MIN);
    }
}
