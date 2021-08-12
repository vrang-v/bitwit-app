package com.app.bitwit.data.source.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.app.bitwit.data.source.local.entity.Candlestick;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

import java.util.List;

@Dao
public interface CandlestickDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCandlestick(Candlestick candlestick);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCandlesticks(List<Candlestick> candlesticks);
    
    @Query("select * from Candlestick where Candlestick.ticker = :ticker and Candlestick.interval = :interval order by Candlestick.dataTime desc limit :size")
    LiveData<List<Candlestick>> loadByTickerAndInterval(String ticker, String interval, int size);
    
    @Query("select Candlestick.dataTime from Candlestick where Candlestick.ticker = :ticker and Candlestick.interval = :interval order by Candlestick.dataTime desc limit 1")
    Maybe<String> getLastDataTime(String ticker, String interval);
}
