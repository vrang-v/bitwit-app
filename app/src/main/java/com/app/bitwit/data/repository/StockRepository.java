package com.app.bitwit.data.repository;

import com.app.bitwit.data.source.local.CandlestickDao;
import com.app.bitwit.data.source.remote.StockServiceClient;
import com.app.bitwit.data.source.remote.dto.request.SearchStockRequest;
import com.app.bitwit.domain.Stock;
import com.app.bitwit.util.HttpUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class StockRepository {
    
    private final StockServiceClient stockServiceClient;
    
    private final CandlestickDao candlestickDao;
    
    public Single<List<Stock>> searchStock(SearchStockRequest request) {
        return stockServiceClient
                .searchStock(request.getKeyword( ))
                .map(HttpUtils::get2xxBody);
    }
}
