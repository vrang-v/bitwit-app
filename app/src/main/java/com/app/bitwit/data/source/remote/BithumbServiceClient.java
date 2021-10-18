package com.app.bitwit.data.source.remote;

import com.app.bitwit.data.source.remote.dto.response.BithumbCandlestickResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BithumbServiceClient {
    
    @GET("/public/candlestick/{ticker}_krw/{interval}")
    Single<Response<BithumbCandlestickResponse>> getCandlestick(@Path("ticker") String ticker, @Path("interval") String interval);
    
}
