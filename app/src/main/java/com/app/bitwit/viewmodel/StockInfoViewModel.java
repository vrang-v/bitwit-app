package com.app.bitwit.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.CandlestickRepository;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.data.repository.VoteRepository;
import com.app.bitwit.data.source.local.entity.Candlestick;
import com.app.bitwit.data.source.local.entity.VoteItem;
import com.app.bitwit.data.source.storage.LocalStorage;
import com.app.bitwit.data.source.storage.LocalStorageKey;
import com.app.bitwit.domain.SelectedCandlestick;
import com.app.bitwit.dto.PostPreviewItem;
import com.app.bitwit.util.livedata.LiveDataAdapter;
import com.app.bitwit.util.livedata.MutableLiveList;
import com.app.bitwit.util.subscription.CompletableSubscription;
import com.app.bitwit.util.subscription.SingleSubscription;
import com.app.bitwit.view.activity.StockInfoActivity;
import com.app.bitwit.viewmodel.common.RxJavaViewModelSupport;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.var;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@HiltViewModel
public class StockInfoViewModel extends RxJavaViewModelSupport implements SnackbarViewModel {
    
    private final VoteRepository        voteRepository;
    private final CandlestickRepository candlestickRepository;
    private final PostRepository        postRepository;
    
    private final LocalStorage localStorage;
    
    private final MutableLiveData<String>              ticker              = new MutableLiveData<>( );
    private final MutableLiveList<Candlestick>         chartData           = new MutableLiveList<>( );
    private final MutableLiveData<List<Entry>>         lineEntries         = new MutableLiveData<>( );
    private final MutableLiveData<List<CandleEntry>>   candleEntries       = new MutableLiveData<>( );
    private final MutableLiveData<String>              chartType           = new MutableLiveData<>( );
    private final MutableLiveData<String>              chartInterval       = new MutableLiveData<>( );
    private final MutableLiveData<SelectedCandlestick> selectedCandlestick = new MutableLiveData<>( );
    private final MutableLiveList<PostPreviewItem>     postPreviewItems    = new MutableLiveList<>( );
    
    private final LiveDataAdapter<VoteItem>          voteItem    = new LiveDataAdapter<>( );
    private final LiveDataAdapter<List<Candlestick>> candlestick = new LiveDataAdapter<>( );
    
    @Inject
    public StockInfoViewModel(VoteRepository voteRepository, CandlestickRepository candlestickRepository, PostRepository postRepository, LocalStorage localStorage) {
        this.voteRepository        = voteRepository;
        this.candlestickRepository = candlestickRepository;
        this.postRepository        = postRepository;
        this.localStorage          = localStorage;
        loadChartType( );
        loadChartInterval( );
        observe(candlestick, candlesticks -> {
            Collections.reverse(candlesticks);
            chartData.postValue(candlesticks);
            postLineEntries(candlesticks);
            postCandleEntries(candlesticks);
        });
    }
    
    public void loadVoteItem(String ticker) {
        voteItem.changeSource(voteRepository.loadLastEndedVoteByTicker(ticker));
    }
    
    public void loadChartData(String ticker, String interval) {
        candlestick.changeSource(candlestickRepository.loadCandlesticks(ticker, interval, 500));
    }
    
    private void postLineEntries(List<Candlestick> chartData) {
        var index = new AtomicInteger(0);
        this.lineEntries.postValue(
                chartData.stream( )
                         .map(candlestick -> new Entry(index.getAndAdd(1),
                                 candlestick.getClosingPrice( ).floatValue( ))
                         )
                         .collect(Collectors.toList( ))
        );
    }
    
    private void postCandleEntries(List<Candlestick> chartData) {
        var index = new AtomicInteger(0);
        this.candleEntries.postValue(
                chartData.stream( )
                         .map(candlestick -> new CandleEntry(index.getAndAdd(1),
                                 candlestick.getHighPrice( ).floatValue( ),
                                 candlestick.getLowPrice( ).floatValue( ),
                                 candlestick.getOpenPrice( ).floatValue( ),
                                 candlestick.getClosingPrice( ).floatValue( ))
                         )
                         .collect(Collectors.toList( ))
        );
    }
    
    public void loadChartType( ) {
        this.chartType.postValue(
                localStorage.load(LocalStorageKey.CHART_TYPE, String.class)
                            .orElse(StockInfoActivity.CANDLE_STICK)
        );
    }
    
    public void loadChartInterval( ) {
        this.chartInterval.postValue(
                localStorage.load(LocalStorageKey.CHART_INTERVAL, String.class)
                            .orElse("1m")
        );
    }
    
    @SuppressLint("NewApi")
    public CompletableSubscription refreshChartData(String ticker, String interval) {
        return subscribe(
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
                                case "1d":
                                    return isOldData(localDateTime, 1L, ChronoUnit.DAYS);
                                default:
                                    return false;
                            }
                        })
                        .flatMapCompletable(unused -> candlestickRepository.refreshCandlestick(ticker, interval))
                        .doOnError(e -> setSnackbar("차트 데이터를 가져오는 도중 문제가 발생했어요"))
        );
    }
    
    public SingleSubscription<List<PostPreviewItem>> loadPostPreviews(String ticker) {
        return subscribe(
                postRepository
                        .searchPostByTickers(Collections.singletonList(ticker), 5)
                        .map(posts -> posts.stream( ).map(PostPreviewItem::new).collect(Collectors.toList( )))
                        .doOnSuccess(postPreviewItems::postValue)
        );
    }
    
    public void setTicker(String ticker) {
        this.ticker.postValue(ticker);
    }
    
    public void setChartType(String chartType) {
        localStorage.save(LocalStorageKey.CHART_TYPE, chartType);
        this.chartType.postValue(chartType);
    }
    
    public void setChartInterval(String chartInterval) {
        localStorage.save(LocalStorageKey.CHART_INTERVAL, chartInterval);
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
    
    @SuppressLint("NewApi")
    private boolean isOldData(LocalDateTime lastDataTime, Long amount, TemporalUnit temporalUnit) {
        var nextDataTime = lastDataTime.plus(amount, temporalUnit);
        return ! LocalDateTime.now( ).isBefore(nextDataTime);
    }
    
    @Override
    protected void onCleared( ) {
        voteItem.dispose( );
        candlestick.dispose( );
    }
}
