package com.app.bitwit.data.repository;

import androidx.lifecycle.LiveData;
import com.app.bitwit.data.source.local.CandlestickDao;
import com.app.bitwit.data.source.remote.StockServiceClient;
import com.app.bitwit.domain.Candlestick_;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class StockRepository {
    
    private final StockServiceClient stockServiceClient;
    
    private final CandlestickDao candlestickDao;
}
