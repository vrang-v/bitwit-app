package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.request.StockRequest;
import com.app.bitwit.domain.Candlestick_;
import com.app.bitwit.domain.Stock;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface StockServiceClient {
    @POST("/api/v1/stocks")
    Single<Response<Stock>> createStock(@Body StockRequest stockRequest);
    
    @GET("/api/v1/stocks/{stockId}")
    Single<Response<Stock>> getStock(@Path("stockId") Long stockId);
    
    @GET("/api/v1/stocks/{ticker}/bithumb/chart/24h")
    Single<Response<List<Candlestick_>>> getCandleSticks(
            @Path("ticker") String ticker,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );
}
