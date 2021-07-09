package com.app.bitwit.webclient;

import com.app.bitwit.webclient.dto.request.AccountRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountServiceClient
{
    @POST("/accounts")
    Call<Void> createAccount(@Body AccountRequest account);
}
