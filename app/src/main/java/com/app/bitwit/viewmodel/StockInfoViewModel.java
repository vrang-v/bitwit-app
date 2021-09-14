package com.app.bitwit.viewmodel;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.adapter.PostPreviewItem;
import com.app.bitwit.data.repository.CandlestickRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.domain.SelectedCandlestick;
import com.app.bitwit.util.MutableObserver;
import com.github.mikephil.charting.data.Entry;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.app.bitwit.util.LiveDataUtils.observeUntilNotEmpty;

@Getter
@HiltViewModel
public class StockInfoViewModel extends DisposableViewModel {
    
    private final VoteRepository        voteRepository;
    private final CandlestickRepository candlestickRepository;
    private final PostRepository        postRepository;
    
    private final MutableLiveData<String>              ticker              = new MutableLiveData<>( );
    private final MutableLiveData<String>              chartInterval       = new MutableLiveData<>( );
    private final MutableLiveData<SelectedCandlestick> selectedCandlestick = new MutableLiveData<>( );
    
    private final MutableLiveData<List<PostPreviewItem>> postPreviewItems = new MutableLiveData<>( );
    
    private final MutableLiveData<VoteItem>    voteItem = new MutableLiveData<>( );
    private final MutableLiveData<List<Entry>> entries  = new MutableLiveData<>( );
    
    private final MutableObserver<VoteItem> voteItemObserver = new MutableObserver<>(voteItem::postValue);
    
    private final List<Candlestick> chartData = new ArrayList<>( );
    
    @Inject
    public StockInfoViewModel(VoteRepository voteRepository, CandlestickRepository candlestickRepository, PostRepository postRepository) {
        this.voteRepository        = voteRepository;
        this.candlestickRepository = candlestickRepository;
        this.postRepository        = postRepository;
    }
    
    public void setTicker(String ticker) {
        this.ticker.postValue(ticker);
    }
    
    public void setChartInterval(String chartInterval) {
        this.chartInterval.postValue(chartInterval);
    }
    
    public void setSelectedCandlestick(int index) {
        if (chartData.isEmpty( ) || index >= chartData.size( )) { return; }
        
        this.selectedCandlestick.postValue(
                new SelectedCandlestick(
                        chartData.get(index),
                        chartData.get(0),
                        index == 0 ? chartData.get(0) : chartData.get(index - 1)
                )
        );
    }
    
    public void loadVoteItem(String ticker) {
        voteItemObserver.observe(voteRepository.loadLastEndedVoteByTicker(ticker));
    }
    
    public void loadChart(String ticker, String interval) {
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
    
    @SuppressLint("NewApi")
    public void refreshChart(String ticker, String interval) {
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
                        .flatMapCompletable(unused -> candlestickRepository.refreshCandlestick(ticker, interval))
                        .subscribeOn(Schedulers.io( ))
                        .observeOn(AndroidSchedulers.mainThread( ))
                        .subscribe(( ) -> { }, e -> Log.e("ERROR", "refreshChart: ", e))
        );
    }
    
    public void loadPosts(String ticker) {
        addDisposable(
                postRepository.searchPostByTickers(Collections.singletonList(ticker))
                              .map(posts -> posts.stream( ).map(PostPreviewItem::new).collect(Collectors.toList( )))
                              .subscribeOn(Schedulers.io( ))
                              .observeOn(AndroidSchedulers.mainThread( ))
                              .subscribe(postPreviewItems::postValue, e -> Log.e("ERROR", "loadPosts", e)));
    }
    
    @SuppressLint("NewApi")
    private boolean isOldData(LocalDateTime lastDataTime, Long amount, TemporalUnit temporalUnit) {
        var now          = LocalDateTime.now( );
        var nextDataTime = lastDataTime.plus(amount, temporalUnit);
        return now.isAfter(nextDataTime) || now.isEqual(nextDataTime);
    }
    
    @Override
    protected void onCleared( ) {
        voteItemObserver.dispose( );
    }
}
