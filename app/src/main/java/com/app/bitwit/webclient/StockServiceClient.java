package com.app.bitwit.webclient;

import com.app.bitwit.domain.Stock;
import com.app.bitwit.webclient.dto.request.StockRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StockServiceClient
{
    @POST("/stocks")
    Call<Stock> createStock(@Body StockRequest stockRequest);
    
    @GET("/stocks/{stockId}")
    Call<Stock> getStock(@Path("stockId") Long stockId);
}
